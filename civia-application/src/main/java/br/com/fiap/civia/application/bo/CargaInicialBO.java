package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.SituacaoServico;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;

/**
 * Carga de dados de demonstracao (SIMULADOS - a GS proibe dados pessoais reais) na regiao da Se.
 * So executa com as tabelas vazias e passa pelas fabricas do dominio, respeitando as mesmas regras de um cadastro.
 * Os mesmos dados estao em db/02_dados.sql para carga manual.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CargaInicialBO {

    private final ServicoDAO servicoDAO;
    private final TotemDAO totemDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final Clock clock;

    @Transactional
    public void carregarSeNecessario() {
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        if (totemDAO.contar() == 0) {
            totemDAO.salvar(Totem.novo("TOTEM-SE-01", "Praca da Se, em frente a Catedral",
                    new Coordenada(-23.550520, -46.633308), true));
            totemDAO.salvar(Totem.novo("TOTEM-ANHANGABAU-01", "Vale do Anhangabau, proximo a escadaria",
                    new Coordenada(-23.545210, -46.636780), true));
            totemDAO.salvar(Totem.novo("TOTEM-PQDPEDRO-01", "Terminal Parque Dom Pedro II",
                    new Coordenada(-23.549540, -46.627880), false));
            log.info(">>> CIVIA: 3 totens de demonstracao carregados");
        }

        if (servicoDAO.contar() > 0) {
            return;
        }
        Servico abrigoLiberdade = salvar("Centro de Acolhida Liberdade (simulado)", TipoServico.ABRIGO,
                "Rua dos Estudantes, 200 - Liberdade", -23.555870, -46.635210, "18:00", "08:00",
                EnumSet.of(PerfilAtendido.HOMEM, PerfilAtendido.IDOSO), true, agora);
        Servico abrigoMulheres = salvar("Casa de Acolhida Mulheres Se (simulado)", TipoServico.ABRIGO,
                "Rua Tabatinguera, 150 - Se", -23.551910, -46.630950, "17:00", "08:00",
                EnumSet.of(PerfilAtendido.MULHER, PerfilAtendido.FAMILIA, PerfilAtendido.LGBT), true, agora);
        Servico abrigoAnimais = salvar("Abrigo Gloria com Pets (simulado)", TipoServico.ABRIGO,
                "Rua da Gloria, 410 - Liberdade", -23.557400, -46.634100, "18:00", "07:00",
                EnumSet.of(PerfilAtendido.HOMEM, PerfilAtendido.MULHER, PerfilAtendido.COM_ANIMAL), true, agora);
        salvar("Abrigo Bela Vista (simulado, aguardando aprovacao)", TipoServico.ABRIGO,
                "Rua Santo Antonio, 600 - Bela Vista", -23.554300, -46.642100, "18:00", "08:00",
                EnumSet.of(PerfilAtendido.HOMEM), false, agora);
        Servico refeitorio = salvar("Restaurante Comunitario Se (simulado)", TipoServico.REFEICAO,
                "Rua Roberto Simonsen, 80 - Se", -23.549600, -46.631200, "11:00", "14:00",
                Set.of(), true, agora);
        salvar("Sopao Solidario Anhangabau (simulado)", TipoServico.REFEICAO,
                "Rua Formosa, 120 - Centro", -23.546300, -46.637900, "18:00", "20:00",
                Set.of(), true, agora);
        Servico banho = salvar("Nucleo de Convivencia Banho Se (simulado)", TipoServico.BANHO,
                "Rua do Carmo, 50 - Se", -23.550900, -46.632100, "07:00", "17:00",
                Set.of(), true, agora);
        salvar("Banho Solidario Glicerio (simulado)", TipoServico.BANHO,
                "Rua do Glicerio, 300 - Liberdade", -23.555100, -46.629800, "08:00", "12:00",
                Set.of(), true, agora);
        salvar("Centro POP Se (simulado)", TipoServico.ATENDIMENTO,
                "Rua Riachuelo, 268 - Se", -23.551700, -46.637000, "08:00", "17:00",
                Set.of(), true, agora);
        salvar("Centro POP Bras (simulado)", TipoServico.ATENDIMENTO,
                "Rua Prof. Batista de Andrade, 20 - Bras", -23.544800, -46.622900, "08:00", "17:00",
                Set.of(), true, agora);

        registrar(abrigoLiberdade, 6, null, agora.minusMinutes(20));
        registrar(abrigoMulheres, 2, null, agora.minusMinutes(45));
        registrar(abrigoAnimais, 0, null, agora.minusHours(3));
        registrar(refeitorio, null, SituacaoServico.FILA_GRANDE, agora.minusMinutes(10));
        registrar(banho, null, SituacaoServico.ABERTO, agora.minusMinutes(30));
        log.info(">>> CIVIA: 10 servicos e 5 atualizacoes de demonstracao carregados");
    }

    private Servico salvar(String nome, TipoServico tipo, String endereco, double lat, double lon,
                           String abertura, String fechamento, Set<PerfilAtendido> perfis, boolean aprovado,
                           LocalDateTime agora) {
        return servicoDAO.salvar(Servico.novo(nome, tipo, endereco, new Coordenada(lat, lon),
                new HorarioFuncionamento(LocalTime.parse(abertura), LocalTime.parse(fechamento)),
                perfis, aprovado, agora));
    }

    private void registrar(Servico servico, Integer vagas, SituacaoServico situacao, LocalDateTime dataHora) {
        atualizacaoDAO.salvar(AtualizacaoDisponibilidade.registrar(servico, vagas, situacao, dataHora));
    }
}
