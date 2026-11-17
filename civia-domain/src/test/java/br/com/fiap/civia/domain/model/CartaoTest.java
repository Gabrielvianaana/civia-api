package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** RN07 (cartao anonimo), RN11 (saldo so sai em trocas, nunca negativo) e RN12 (substituicao). */
class CartaoTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 10, 0);

    private static Cartao ativo(int saldo) {
        return Cartao.reconstituir(1L, "CVA7K2M9", 10L, null, AGORA.minusDays(5), StatusCartao.ATIVO, saldo);
    }

    @Test
    void cartaoNovoComecaAtivoESemSaldo() {
        Cartao c = Cartao.emitir("CVZ2X8K4", 10L, AGORA);
        assertEquals(StatusCartao.ATIVO, c.getStatus());
        assertEquals(0, c.getSaldoCreditos());
    }

    @Test
    void codigoSoTemLetrasMaiusculasENumeros() {
        assertThrows(DadoInvalidoException.class, () -> Cartao.emitir("joao-123", 10L, AGORA));
    }

    @Test
    void debitoNaoDeixaSaldoNegativo() {
        Cartao c = ativo(30);
        c.debitar(20);
        assertEquals(10, c.getSaldoCreditos());
        assertThrows(RegraNegocioVioladaException.class, () -> c.debitar(11));
    }

    @Test
    void substituicaoBloqueiaOAnteriorETransfereOSaldo() {
        Cartao anterior = ativo(42);
        Cartao novo = Cartao.substituir(anterior, "CVQ7W3E5", 10L, AGORA);
        assertEquals(StatusCartao.BLOQUEADO, anterior.getStatus());
        assertEquals(0, anterior.getSaldoCreditos());
        assertEquals(42, novo.getSaldoCreditos());
        assertEquals(1L, novo.getCartaoAnteriorId());
        assertThrows(RegraNegocioVioladaException.class, () -> anterior.creditar(5));
    }

    @Test
    void bloquearComSaldoExigeSubstituicao() {
        assertThrows(RegraNegocioVioladaException.class, () -> ativo(5).atualizar(10L, StatusCartao.BLOQUEADO));
    }
}
