package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeRequest;
import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.PerfilAcesso;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static br.com.fiap.civia.application.bo.Dados.AGORA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** RN03 aplicada na gravacao: abrigo recebe vagas e grava a faixa; demais tipos gravam a situacao. */
class AtualizacaoDisponibilidadeBOTest {

    private AtualizacaoDisponibilidadeBO bo;

    @BeforeEach
    void setUp() {
        AtualizacaoDisponibilidadeDAO atualizacaoDAO = mock(AtualizacaoDisponibilidadeDAO.class);
        ServicoDAO servicoDAO = mock(ServicoDAO.class);
        UsuarioPainelDAO usuarioDAO = mock(UsuarioPainelDAO.class);
        bo = new AtualizacaoDisponibilidadeBO(atualizacaoDAO, servicoDAO, usuarioDAO, Dados.RELOGIO);
        when(servicoDAO.buscarPorId(1L)).thenReturn(Optional.of(Dados.servico(1, Dados.ABRIGO, 100)));
        when(servicoDAO.buscarPorId(2L)).thenReturn(Optional.of(Dados.servico(2, Dados.REFEICAO, 100)));
        when(usuarioDAO.buscarPorId(7L)).thenReturn(Optional.of(
                UsuarioPainel.reconstituir(7L, 1L, "Ana (simulada)", "ana@civia.exemplo.org", PerfilAcesso.EQUIPE, true)));
        when(atualizacaoDAO.salvar(any())).thenAnswer(inv -> {
            AtualizacaoDisponibilidade a = inv.getArgument(0);
            return AtualizacaoDisponibilidade.reconstituir(50L, a.getServicoId(), a.getUsuarioId(), a.getStatus(),
                    a.getVagas(), a.getOrigem(), a.getDataHora());
        });
    }

    @Test
    void abrigoGravaAFaixaCorrespondenteAsVagas() {
        AtualizacaoDisponibilidadeResponse r = bo.registrar(new AtualizacaoDisponibilidadeRequest(1L, "CENTRAL_VAGAS", null, 3, null, null));
        assertEquals("POUCAS_VAGAS", r.status());
        assertEquals(3, r.vagas());
        assertEquals(AGORA, r.dataHora());
    }

    @Test
    void refeicaoLotadaGravaZeroVagas() {
        AtualizacaoDisponibilidadeResponse r = bo.registrar(new AtualizacaoDisponibilidadeRequest(2L, null, 7L, null, "LOTADO", null));
        assertEquals("LOTADO", r.status());
        assertEquals(0, r.vagas());
        assertEquals("PAINEL", r.origem());
    }

    @Test
    void refeicaoAbertaNaoGravaVagas() {
        assertNull(bo.registrar(new AtualizacaoDisponibilidadeRequest(2L, null, 7L, null, "ABERTO", null)).vagas());
    }

    @Test
    void abrigoSemVagasERecusado() {
        assertThrows(RegraNegocioException.class,
                () -> bo.registrar(new AtualizacaoDisponibilidadeRequest(1L, "CENTRAL_VAGAS", null, null, null, null)));
    }

    @Test
    void refeicaoComVagasERecusada() {
        assertThrows(RegraNegocioException.class,
                () -> bo.registrar(new AtualizacaoDisponibilidadeRequest(2L, null, 7L, 5, null, null)));
    }

    @Test
    void dataNoFuturoERecusada() {
        assertThrows(RegraNegocioException.class,
                () -> bo.registrar(new AtualizacaoDisponibilidadeRequest(2L, null, 7L, null, "ABERTO", AGORA.plusHours(1))));
    }
}
