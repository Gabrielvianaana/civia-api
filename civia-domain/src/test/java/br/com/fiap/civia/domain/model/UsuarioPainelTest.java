package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioPainelTest {

    private final Servico servicoDaOrg1 = Fixtures.servico(1, Fixtures.REFEICAO, null, 1);

    @Test
    void equipeSoAtualizaServicosDaPropriaOrganizacao() {
        assertDoesNotThrow(() -> usuario(1L, PerfilAcesso.EQUIPE, true).garantirPodeAtualizar(servicoDaOrg1));
        assertThrows(RegraNegocioVioladaException.class,
                () -> usuario(2L, PerfilAcesso.EQUIPE, true).garantirPodeAtualizar(servicoDaOrg1));
    }

    @Test
    void gestorAtualizaQualquerServicoECooperativaNenhum() {
        assertDoesNotThrow(() -> usuario(9L, PerfilAcesso.GESTOR, true).garantirPodeAtualizar(servicoDaOrg1));
        assertThrows(RegraNegocioVioladaException.class,
                () -> usuario(1L, PerfilAcesso.COOPERATIVA, true).garantirPodeAtualizar(servicoDaOrg1));
    }

    @Test
    void usuarioInativoNaoAtualiza() {
        assertThrows(RegraNegocioVioladaException.class,
                () -> usuario(1L, PerfilAcesso.GESTOR, false).garantirPodeAtualizar(servicoDaOrg1));
    }

    @Test
    void emailInvalidoERecusado() {
        assertThrows(DadoInvalidoException.class,
                () -> UsuarioPainel.novo(1L, "Ana", "ana-sem-arroba", PerfilAcesso.EQUIPE, true));
    }

    private static UsuarioPainel usuario(Long organizacaoId, PerfilAcesso perfil, boolean ativo) {
        return UsuarioPainel.reconstituir(5L, organizacaoId, "Pessoa (simulada)", "pessoa@civia.exemplo.org", perfil, ativo);
    }
}
