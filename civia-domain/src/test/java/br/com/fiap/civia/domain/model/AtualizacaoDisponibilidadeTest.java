package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import static br.com.fiap.civia.domain.model.Fixtures.AGORA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Invariantes que espelham os CHECKs ck_atu_usuario e ck_atu_faixa de T_ATUALIZACAO. */
class AtualizacaoDisponibilidadeTest {

    private final Servico abrigo = Fixtures.servico(1, Fixtures.ABRIGO, 10, 1);
    private final Servico refeicao = Fixtures.servico(2, Fixtures.REFEICAO, null, 1);

    @Test
    void painelExigeUsuarioECentralNaoTemUsuario() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(abrigo, null,
                StatusDisponibilidade.TEM_VAGA, 5, OrigemAtualizacao.PAINEL, AGORA));
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(abrigo, 7L,
                StatusDisponibilidade.TEM_VAGA, 5, OrigemAtualizacao.CENTRAL_VAGAS, AGORA));
    }

    @Test
    void centralDeVagasSoInformaAbrigo() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(refeicao, null,
                StatusDisponibilidade.ABERTO, null, OrigemAtualizacao.CENTRAL_VAGAS, AGORA));
    }

    @Test
    void statusDeveCorresponderAFaixaDeVagas() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(abrigo, null,
                StatusDisponibilidade.TEM_VAGA, 2, OrigemAtualizacao.CENTRAL_VAGAS, AGORA));
        AtualizacaoDisponibilidade a = AtualizacaoDisponibilidade.registrar(abrigo, null,
                StatusDisponibilidade.POUCAS_VAGAS, 2, OrigemAtualizacao.CENTRAL_VAGAS, AGORA);
        assertEquals(2, a.getVagas());
    }

    @Test
    void vagasNaoExcedemACapacidade() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(abrigo, null,
                StatusDisponibilidade.TEM_VAGA, 11, OrigemAtualizacao.CENTRAL_VAGAS, AGORA));
    }

    @Test
    void abertoNaoRegistraVagas() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade.registrar(refeicao, 3L,
                StatusDisponibilidade.ABERTO, 5, OrigemAtualizacao.PAINEL, AGORA));
    }
}
