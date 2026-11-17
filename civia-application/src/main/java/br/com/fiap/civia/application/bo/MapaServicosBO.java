package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.MapaServicosResponse;
import br.com.fiap.civia.application.dto.ServicoProximoResponse;
import br.com.fiap.civia.application.exception.DadosInvalidosException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.HorarioDAO;
import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.GradeHorario;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.model.Totem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO do mapa de servicos consultado no totem: GET /totens/{id}/servicos?tipo=&perfil= (RF02, RF03, RF04, US02).
 * <ul>
 *   <li><b>RN06</b> - somente servicos APROVADOS pelo gestor aparecem.</li>
 *   <li>O filtro de perfil se aplica apenas a abrigos (US04).</li>
 *   <li>Ordenacao: disponiveis, depois lotados/fechados, por ultimo sem atualizacao recente (RN05);
 *       dentro de cada grupo, do mais proximo ao mais distante. No maximo 3 servicos.</li>
 * </ul>
 * {@code tipo} e {@code perfil} aceitam o id ou o nome (sem diferenciar acentos/maiusculas).
 */
@Service
@RequiredArgsConstructor
public class MapaServicosBO {

    public static final int LIMITE_RESULTADOS = 3;
    /** Velocidade media de caminhada: 4,8 km/h = 80 metros por minuto. */
    public static final double METROS_POR_MINUTO = 80d;

    static final String ORIENTACAO_ABRIGO =
            "A entrada no abrigo e feita por encaminhamento: procure o Centro POP mais proximo ou ligue 156.";
    static final String ORIENTACAO_SEM_RESULTADO =
            "Nenhum servico encontrado agora. Procure o Centro POP mais proximo ou ligue 156.";

    private final TotemDAO totemDAO;
    private final ServicoDAO servicoDAO;
    private final TipoServicoDAO tipoDAO;
    private final PerfilAtendidoDAO perfilDAO;
    private final HorarioDAO horarioDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final DisponibilidadeBO disponibilidadeBO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public MapaServicosResponse consultar(Long totemId, String tipo, String perfil) {
        Totem totem = totemDAO.buscarPorId(totemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Totem " + totemId + " nao encontrado."));
        executar(totem::garantirDisponivelParaConsulta);

        TipoServico tipoFiltro = resolver(tipo, "tipo", tipoDAO.listar(), TipoServico::getId, TipoServico::temNome);
        PerfilAtendido perfilFiltro = resolver(perfil, "perfil", perfilDAO.listar(), PerfilAtendido::getId,
                PerfilAtendido::temDescricao);
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        List<Servico> candidatos = servicoDAO.listar(tipoFiltro == null ? null : tipoFiltro.getId(), StatusAprovacao.APROVADO, null)
                .stream()
                .filter(Servico::estaAprovado)
                .filter(s -> perfilFiltro == null || !s.ehAbrigo() || s.atendePerfil(perfilFiltro))
                .toList();
        List<Long> ids = candidatos.stream().map(Servico::getId).toList();
        Map<Long, List<Horario>> horarios = horarioDAO.listarPorServicos(ids);
        Map<Long, AtualizacaoDisponibilidade> ultimas = atualizacaoDAO.buscarMaisRecentesPorServicos(ids);

        List<ServicoProximoResponse> resultado = candidatos.stream()
                .map(s -> avaliar(s, totem, new GradeHorario(s.isFunciona24h(), horarios.get(s.getId())),
                        Optional.ofNullable(ultimas.get(s.getId())), agora))
                .sorted(Comparator.comparingInt((Avaliado a) -> a.disponibilidade().status().getGrupoOrdenacao())
                        .thenComparingDouble(Avaliado::distanciaMetros))
                .limit(LIMITE_RESULTADOS)
                .map(a -> paraResponse(a, agora))
                .toList();

        return new MapaServicosResponse(totem.getId(), totem.getCodigo(),
                tipoFiltro == null ? null : tipoFiltro.getNome(),
                perfilFiltro == null ? null : perfilFiltro.getDescricao(),
                agora, resultado, orientacao(tipoFiltro, resultado));
    }

    /** Aceita o id numerico ou o nome; vazio significa "sem filtro". */
    private static <T> T resolver(String valor, String campo, List<T> opcoes, Function<T, Integer> id,
                                  java.util.function.BiPredicate<T, String> temNome) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        Predicate<T> corresponde = valor.trim().matches("\\d+")
                ? o -> id.apply(o).equals(Integer.valueOf(valor.trim()))
                : o -> temNome.test(o, valor);
        return opcoes.stream().filter(corresponde).findFirst()
                .orElseThrow(() -> new DadosInvalidosException("Valor '" + valor + "' invalido para " + campo + "."));
    }

    private Avaliado avaliar(Servico servico, Totem totem, GradeHorario grade, Optional<AtualizacaoDisponibilidade> ultima,
                             LocalDateTime agora) {
        double distancia = totem.getCoordenada().distanciaEmMetrosAte(servico.getCoordenada());
        return new Avaliado(servico, disponibilidadeBO.calcular(servico, grade, ultima, agora), distancia);
    }

    private static ServicoProximoResponse paraResponse(Avaliado a, LocalDateTime agora) {
        Servico s = a.servico();
        Disponibilidade d = a.disponibilidade();
        Long minutosDesde = d.dataReferencia() == null ? null : Math.max(0, Duration.between(d.dataReferencia(), agora).toMinutes());
        return new ServicoProximoResponse(s.getId(), s.getNome(), s.getTipo().getNome(), s.getEndereco().formatado(),
                s.getCoordenada().latitude(), s.getCoordenada().longitude(), Math.round(a.distanciaMetros()),
                minutosCaminhada(a.distanciaMetros()), d.status().name(), d.mensagem(), d.origem().name(),
                d.dataReferencia(), minutosDesde);
    }

    /** Estimativa a pe pela distancia em linha reta (minimo de 1 minuto). */
    static long minutosCaminhada(double distanciaMetros) {
        return Math.max(1, (long) Math.ceil(distanciaMetros / METROS_POR_MINUTO));
    }

    private static String orientacao(TipoServico tipo, List<ServicoProximoResponse> resultado) {
        if (resultado.isEmpty()) {
            return ORIENTACAO_SEM_RESULTADO;
        }
        boolean temAbrigo = (tipo != null && tipo.ehAbrigo())
                || resultado.stream().anyMatch(r -> TipoServico.NOME_ABRIGO.equals(TipoServico.normalizar(r.tipoServico())));
        return temAbrigo ? ORIENTACAO_ABRIGO : null;
    }

    private record Avaliado(Servico servico, Disponibilidade disponibilidade, double distanciaMetros) {
    }
}
