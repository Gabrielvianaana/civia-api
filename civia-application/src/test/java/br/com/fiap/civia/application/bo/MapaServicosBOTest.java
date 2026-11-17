package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.MapaServicosResponse;
import br.com.fiap.civia.application.dto.ServicoProximoResponse;
import br.com.fiap.civia.application.exception.DadosInvalidosException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.HorarioDAO;
import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.domain.model.StatusTotem;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.fiap.civia.application.bo.Dados.AGORA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MapaServicosBOTest {

    private TotemDAO totemDAO;
    private ServicoDAO servicoDAO;
    private AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private MapaServicosBO bo;

    @BeforeEach
    void setUp() {
        totemDAO = mock(TotemDAO.class);
        servicoDAO = mock(ServicoDAO.class);
        atualizacaoDAO = mock(AtualizacaoDisponibilidadeDAO.class);
        TipoServicoDAO tipoDAO = mock(TipoServicoDAO.class);
        PerfilAtendidoDAO perfilDAO = mock(PerfilAtendidoDAO.class);
        HorarioDAO horarioDAO = mock(HorarioDAO.class);
        bo = new MapaServicosBO(totemDAO, servicoDAO, tipoDAO, perfilDAO, horarioDAO, atualizacaoDAO,
                new DisponibilidadeBO(), Dados.RELOGIO);
        when(tipoDAO.listar()).thenReturn(List.of(Dados.ABRIGO, Dados.REFEICAO, Dados.BANHO, Dados.ATENDIMENTO));
        when(perfilDAO.listar()).thenReturn(List.of(Dados.HOMEM, Dados.MULHER));
        when(horarioDAO.listarPorServicos(any())).thenReturn(Map.of());
        when(atualizacaoDAO.buscarMaisRecentesPorServicos(any())).thenReturn(Map.of());
        when(totemDAO.buscarPorId(1L)).thenReturn(Optional.of(totem(1L, StatusTotem.ONLINE)));
    }

    private static Totem totem(Long id, StatusTotem status) {
        return Totem.reconstituir(id, "TOT-SE-01", new Endereco("Praca da Se", null, "Se"), Dados.PRACA_DA_SE, status,
                true, LocalDate.of(2026, 9, 1), null);
    }

    private static List<Long> ids(MapaServicosResponse r) {
        return r.servicos().stream().map(ServicoProximoResponse::servicoId).toList();
    }

    @Test
    void rn06ConsultaSomenteServicosAprovados() {
        when(servicoDAO.listar(2, StatusAprovacao.APROVADO, null)).thenReturn(List.of(
                Dados.servico(1, Dados.REFEICAO, 100),
                Dados.servico(2, Dados.REFEICAO, 50, StatusAprovacao.PENDENTE)));

        MapaServicosResponse r = bo.consultar(1L, "Refeicao", null);

        verify(servicoDAO).listar(2, StatusAprovacao.APROVADO, null);
        assertEquals(List.of(1L), ids(r));
    }

    @Test
    void tipoAceitaIdOuNomeSemAcento() {
        when(servicoDAO.listar(2, StatusAprovacao.APROVADO, null)).thenReturn(List.of(Dados.servico(1, Dados.REFEICAO, 100)));
        assertEquals(List.of(1L), ids(bo.consultar(1L, "2", null)));
        assertEquals(List.of(1L), ids(bo.consultar(1L, "REFEIÇÃO", null)));
    }

    @Test
    void retornaNoMaximoTresOrdenadosPorDistancia() {
        when(servicoDAO.listar(2, StatusAprovacao.APROVADO, null)).thenReturn(List.of(
                Dados.servico(1, Dados.REFEICAO, 900), Dados.servico(2, Dados.REFEICAO, 300),
                Dados.servico(3, Dados.REFEICAO, 1500), Dados.servico(4, Dados.REFEICAO, 100)));

        MapaServicosResponse r = bo.consultar(1L, "Refeicao", null);

        assertEquals(List.of(4L, 2L, 1L), ids(r));
        assertEquals(100, r.servicos().getFirst().distanciaMetros(), 2);
        assertEquals(2, r.servicos().getFirst().minutosCaminhada());
    }

    @Test
    void filtroDePerfilAplicaSomenteAAbrigos() {
        when(servicoDAO.listar(null, StatusAprovacao.APROVADO, null)).thenReturn(List.of(
                Dados.servico(1, Dados.ABRIGO, 100, Dados.HOMEM),
                Dados.servico(2, Dados.ABRIGO, 200, Dados.MULHER),
                Dados.servico(3, Dados.BANHO, 300)));

        MapaServicosResponse r = bo.consultar(1L, null, "Mulher");

        assertEquals(List.of(2L, 3L), ids(r).stream().sorted().toList());
        assertEquals("Mulher", r.perfilFiltrado());
        assertEquals(MapaServicosBO.ORIENTACAO_ABRIGO, r.orientacao());
    }

    @Test
    void lotadoESemAtualizacaoVaoParaOFim() {
        when(servicoDAO.listar(1, StatusAprovacao.APROVADO, null)).thenReturn(List.of(
                Dados.servico(1, Dados.ABRIGO, 100), Dados.servico(2, Dados.ABRIGO, 200), Dados.servico(3, Dados.ABRIGO, 900)));
        when(atualizacaoDAO.buscarMaisRecentesPorServicos(anyCollection())).thenReturn(Map.of(
                1L, Dados.vagas(1, 10, AGORA.minusHours(3)),
                2L, Dados.vagas(2, 0, AGORA.minusMinutes(10)),
                3L, Dados.vagas(3, 8, AGORA.minusMinutes(20))));

        MapaServicosResponse r = bo.consultar(1L, "Abrigo", null);

        assertEquals(List.of(3L, 2L, 1L), ids(r));
        assertEquals(List.of("TEM_VAGA", "LOTADO", "SEM_ATUALIZACAO_RECENTE"),
                r.servicos().stream().map(ServicoProximoResponse::status).toList());
        assertEquals(20L, r.servicos().getFirst().minutosDesdeAtualizacao());
    }

    @Test
    void semResultadoOrientaOProximoPasso() {
        when(servicoDAO.listar(3, StatusAprovacao.APROVADO, null)).thenReturn(List.of());
        MapaServicosResponse r = bo.consultar(1L, "Banho", null);
        assertTrue(r.servicos().isEmpty());
        assertNotNull(r.orientacao());
    }

    @Test
    void totemEmManutencaoNaoConsulta() {
        when(totemDAO.buscarPorId(2L)).thenReturn(Optional.of(totem(2L, StatusTotem.MANUTENCAO)));
        assertThrows(RegraNegocioException.class, () -> bo.consultar(2L, null, null));
    }

    @Test
    void totemInexistenteRetornaNaoEncontrado() {
        when(totemDAO.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> bo.consultar(99L, null, null));
    }

    @Test
    void tipoInexistenteEhRecusado() {
        assertThrows(DadosInvalidosException.class, () -> bo.consultar(1L, "Lavanderia", null));
    }
}
