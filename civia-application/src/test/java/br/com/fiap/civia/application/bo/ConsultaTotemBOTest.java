package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
import br.com.fiap.civia.application.dto.ServicoProximoResponse;
import br.com.fiap.civia.application.exception.DadosInvalidosException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultaTotemBOTest {

    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    /** Quarta-feira, 19h00 em Sao Paulo. */
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 19, 0);
    private static final Coordenada PRACA_DA_SE = new Coordenada(-23.550520, -46.633308);

    private TotemDAO totemDAO;
    private ServicoDAO servicoDAO;
    private AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private ConsultaTotemBO bo;

    @BeforeEach
    void setUp() {
        totemDAO = mock(TotemDAO.class);
        servicoDAO = mock(ServicoDAO.class);
        atualizacaoDAO = mock(AtualizacaoDisponibilidadeDAO.class);
        Clock clock = Clock.fixed(AGORA.atZone(SP).toInstant(), SP);
        bo = new ConsultaTotemBO(totemDAO, servicoDAO, atualizacaoDAO, new DisponibilidadeBO(), clock);
        when(totemDAO.buscarPorId(1L)).thenReturn(Optional.of(Totem.reconstituir(1L, "TOTEM-SE-01",
                "Praca da Se", PRACA_DA_SE, true)));
        when(atualizacaoDAO.buscarMaisRecentesPorServicos(any())).thenReturn(Map.of());
    }

    /** Servico a {@code metrosAoSul} metros ao sul da Praca da Se (0,001 grau ~ 111 m). */
    private static Servico servico(long id, TipoServico tipo, double metrosAoSul, boolean aprovado,
                                   PerfilAtendido... perfis) {
        double lat = PRACA_DA_SE.latitude() - metrosAoSul / 111_195d;
        return Servico.reconstituir(id, "Servico " + id, tipo, "Rua " + id,
                new Coordenada(lat, PRACA_DA_SE.longitude()),
                new HorarioFuncionamento(LocalTime.of(18, 0), LocalTime.of(8, 0)),
                perfis.length == 0 ? Set.of() : Set.of(perfis), aprovado, AGORA.minusDays(30));
    }

    private static AtualizacaoDisponibilidade vagas(long servicoId, int vagas, LocalDateTime quando) {
        return AtualizacaoDisponibilidade.reconstituir(100 + servicoId, servicoId, vagas, null, quando);
    }

    private static List<Long> ids(ConsultaTotemResponse r) {
        return r.servicos().stream().map(ServicoProximoResponse::servicoId).toList();
    }

    @Test
    void rn06ConsultaSomenteServicosAprovados() {
        when(servicoDAO.listar(TipoServico.REFEICAO, Boolean.TRUE)).thenReturn(List.of(
                servico(1, TipoServico.REFEICAO, 100, true),
                servico(2, TipoServico.REFEICAO, 50, false)));

        ConsultaTotemResponse r = bo.consultar(1L, "REFEICAO", null);

        verify(servicoDAO).listar(TipoServico.REFEICAO, Boolean.TRUE);
        assertEquals(List.of(1L), ids(r));
    }

    @Test
    void retornaNoMaximoTresOrdenadosPorDistancia() {
        when(servicoDAO.listar(TipoServico.REFEICAO, Boolean.TRUE)).thenReturn(List.of(
                servico(1, TipoServico.REFEICAO, 900, true),
                servico(2, TipoServico.REFEICAO, 300, true),
                servico(3, TipoServico.REFEICAO, 1500, true),
                servico(4, TipoServico.REFEICAO, 100, true)));

        ConsultaTotemResponse r = bo.consultar(1L, "refeicao", null);

        assertEquals(List.of(4L, 2L, 1L), ids(r));
        ServicoProximoResponse maisProximo = r.servicos().getFirst();
        assertEquals(100, maisProximo.distanciaMetros(), 2);
        assertEquals(2, maisProximo.minutosCaminhada());
    }

    @Test
    void filtroDePerfilAplicaSomenteAAbrigos() {
        when(servicoDAO.listar(TipoServico.ABRIGO, Boolean.TRUE)).thenReturn(List.of(
                servico(1, TipoServico.ABRIGO, 100, true, PerfilAtendido.HOMEM),
                servico(2, TipoServico.ABRIGO, 200, true, PerfilAtendido.MULHER, PerfilAtendido.FAMILIA)));

        ConsultaTotemResponse r = bo.consultar(1L, "ABRIGO", "MULHER");

        assertEquals(List.of(2L), ids(r));
        assertEquals("MULHER", r.perfilFiltrado());
        assertEquals(ConsultaTotemBO.ORIENTACAO_ABRIGO, r.orientacao());
    }

    @Test
    void perfilNaoFiltraServicosQueNaoSaoAbrigo() {
        when(servicoDAO.listar(null, Boolean.TRUE)).thenReturn(List.of(
                servico(1, TipoServico.ABRIGO, 100, true, PerfilAtendido.HOMEM),
                servico(2, TipoServico.BANHO, 200, true)));

        ConsultaTotemResponse r = bo.consultar(1L, null, "MULHER");

        assertEquals(List.of(2L), ids(r));
    }

    @Test
    void lotadoESemAtualizacaoVaoParaOFimMesmoMaisProximos() {
        when(servicoDAO.listar(TipoServico.ABRIGO, Boolean.TRUE)).thenReturn(List.of(
                servico(1, TipoServico.ABRIGO, 100, true, PerfilAtendido.HOMEM),   // sem atualizacao recente
                servico(2, TipoServico.ABRIGO, 200, true, PerfilAtendido.HOMEM),   // lotado
                servico(3, TipoServico.ABRIGO, 900, true, PerfilAtendido.HOMEM))); // tem vaga
        when(atualizacaoDAO.buscarMaisRecentesPorServicos(ArgumentMatchers.anyCollection())).thenReturn(Map.of(
                1L, vagas(1, 10, AGORA.minusHours(3)),
                2L, vagas(2, 0, AGORA.minusMinutes(10)),
                3L, vagas(3, 8, AGORA.minusMinutes(20))));

        ConsultaTotemResponse r = bo.consultar(1L, "ABRIGO", null);

        assertEquals(List.of(3L, 2L, 1L), ids(r));
        assertEquals(List.of("TEM_VAGA", "LOTADO", "SEM_ATUALIZACAO_RECENTE"),
                r.servicos().stream().map(ServicoProximoResponse::status).toList());
        assertEquals(20L, r.servicos().getFirst().minutosDesdeAtualizacao());
    }

    @Test
    void semResultadoOrientaOProximoPasso() {
        when(servicoDAO.listar(eq(TipoServico.BANHO), eq(Boolean.TRUE))).thenReturn(List.of());

        ConsultaTotemResponse r = bo.consultar(1L, "BANHO", null);

        assertTrue(r.servicos().isEmpty());
        assertNotNull(r.orientacao());
    }

    @Test
    void totemInativoNaoConsulta() {
        when(totemDAO.buscarPorId(2L)).thenReturn(Optional.of(Totem.reconstituir(2L, "TOTEM-X", "Local",
                PRACA_DA_SE, false)));
        assertThrows(RegraNegocioException.class, () -> bo.consultar(2L, null, null));
    }

    @Test
    void totemInexistenteRetornaNaoEncontrado() {
        when(totemDAO.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> bo.consultar(99L, null, null));
    }

    @Test
    void tipoInvalidoEhRecusado() {
        assertThrows(DadosInvalidosException.class, () -> bo.consultar(1L, "LAVANDERIA", null));
    }
}
