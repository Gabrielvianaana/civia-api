package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
import br.com.fiap.civia.application.dto.ServicoProximoResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
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

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO da consulta feita no totem: GET /totens/{id}/servicos?tipo=&perfil= (RF02, RF03, RF04, US02).
 * <ul>
 *   <li><b>RN06</b> - somente servicos aprovados pelo gestor aparecem.</li>
 *   <li>O filtro de perfil se aplica apenas a abrigos (US04).</li>
 *   <li>Ordenacao: disponiveis primeiro, depois lotados/fechados, por ultimo sem atualizacao recente (RN05);
 *       dentro de cada grupo, do mais proximo ao mais distante.</li>
 *   <li>Retorna no maximo 3 servicos.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ConsultaTotemBO {

    public static final int LIMITE_RESULTADOS = 3;
    /** Velocidade media de caminhada: 4,8 km/h = 80 metros por minuto. */
    public static final double METROS_POR_MINUTO = 80d;

    static final String ORIENTACAO_ABRIGO =
            "A entrada no abrigo e feita por encaminhamento: procure o Centro POP mais proximo ou ligue 156.";
    static final String ORIENTACAO_SEM_RESULTADO =
            "Nenhum servico encontrado agora. Procure o Centro POP mais proximo ou ligue 156.";

    private final TotemDAO totemDAO;
    private final ServicoDAO servicoDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final DisponibilidadeBO disponibilidadeBO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public ConsultaTotemResponse consultar(Long totemId, String tipo, String perfil) {
        Totem totem = totemDAO.buscarPorId(totemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Totem " + totemId + " nao encontrado."));
        executar(totem::garantirAtivo);

        TipoServico tipoFiltro = Conversor.paraEnum(TipoServico.class, tipo, "tipo");
        PerfilAtendido perfilFiltro = Conversor.paraEnum(PerfilAtendido.class, perfil, "perfil");
        LocalDateTime agora = LocalDateTime.now(clock);

        // RN06: apenas servicos aprovados. Filtro de perfil: somente para abrigos.
        List<Servico> candidatos = servicoDAO.listar(tipoFiltro, Boolean.TRUE).stream()
                .filter(Servico::isAprovado)
                .filter(s -> perfilFiltro == null || !s.ehAbrigo() || s.atendePerfil(perfilFiltro))
                .toList();

        Map<Long, AtualizacaoDisponibilidade> ultimas =
                atualizacaoDAO.buscarMaisRecentesPorServicos(candidatos.stream().map(Servico::getId).toList());

        List<ServicoProximoResponse> resultado = candidatos.stream()
                .map(s -> avaliar(s, totem, Optional.ofNullable(ultimas.get(s.getId())), agora))
                .sorted(Comparator.comparingInt((Avaliado a) -> a.disponibilidade().status().getGrupoOrdenacao())
                        .thenComparingDouble(Avaliado::distanciaMetros))
                .limit(LIMITE_RESULTADOS)
                .map(a -> paraResponse(a, agora))
                .toList();

        return new ConsultaTotemResponse(
                totem.getId(),
                totem.getIdentificacao(),
                totem.getLocal(),
                tipoFiltro == null ? null : tipoFiltro.name(),
                perfilFiltro == null ? null : perfilFiltro.name(),
                agora.truncatedTo(ChronoUnit.SECONDS),
                resultado,
                orientacao(tipoFiltro, resultado));
    }

    private Avaliado avaliar(Servico servico, Totem totem, Optional<AtualizacaoDisponibilidade> ultima,
                             LocalDateTime agora) {
        double distancia = totem.getCoordenada().distanciaEmMetrosAte(servico.getCoordenada());
        return new Avaliado(servico, disponibilidadeBO.calcular(servico, ultima, agora), distancia);
    }

    private static ServicoProximoResponse paraResponse(Avaliado avaliado, LocalDateTime agora) {
        Servico s = avaliado.servico();
        Disponibilidade d = avaliado.disponibilidade();
        Long minutosDesde = d.dataReferencia() == null ? null
                : Math.max(0, Duration.between(d.dataReferencia(), agora).toMinutes());
        return new ServicoProximoResponse(
                s.getId(),
                s.getNome(),
                s.getTipo().name(),
                s.getEndereco(),
                s.getCoordenada().latitude(),
                s.getCoordenada().longitude(),
                Conversor.deHora(s.getHorario().abertura()),
                Conversor.deHora(s.getHorario().fechamento()),
                Math.round(avaliado.distanciaMetros()),
                minutosCaminhada(avaliado.distanciaMetros()),
                d.status().name(),
                d.mensagem(),
                d.origem().name(),
                d.dataReferencia(),
                minutosDesde);
    }

    /** Estimativa a pe pela distancia em linha reta (minimo de 1 minuto). */
    static long minutosCaminhada(double distanciaMetros) {
        return Math.max(1, (long) Math.ceil(distanciaMetros / METROS_POR_MINUTO));
    }

    private static String orientacao(TipoServico tipo, List<ServicoProximoResponse> resultado) {
        if (resultado.isEmpty()) {
            return ORIENTACAO_SEM_RESULTADO;
        }
        boolean temAbrigo = tipo == TipoServico.ABRIGO
                || resultado.stream().anyMatch(r -> TipoServico.ABRIGO.name().equals(r.tipo()));
        return temAbrigo ? ORIENTACAO_ABRIGO : null;
    }

    private record Avaliado(Servico servico, Disponibilidade disponibilidade, double distanciaMetros) {
    }
}
