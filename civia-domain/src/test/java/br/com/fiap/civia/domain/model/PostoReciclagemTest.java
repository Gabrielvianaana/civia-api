package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostoReciclagemTest {

    private static PostoReciclagem posto(StatusPosto status) {
        return PostoReciclagem.reconstituir(1L, 1L, 4L, new BigDecimal("100"), new BigDecimal("10"), status);
    }

    @Test
    void novoPostoUsaLimiteDiarioPadraoDe10Kg() {
        PostoReciclagem p = PostoReciclagem.novo(1L, 4L, new BigDecimal("100"), null, null);
        assertEquals(new BigDecimal("10.00"), p.getLimiteDiarioKg());
        assertEquals(StatusPosto.ATIVO, p.getStatus());
    }

    @Test
    void postoCheioOuEmManutencaoNaoRecebeEntregas() {
        assertThrows(RegraNegocioVioladaException.class, () -> posto(StatusPosto.CHEIO).garantirRecebeEntregas());
        assertThrows(RegraNegocioVioladaException.class, () -> posto(StatusPosto.MANUTENCAO).garantirRecebeEntregas());
    }

    @Test
    void entregaNaoPassaDaCapacidade() {
        PostoReciclagem p = posto(StatusPosto.ATIVO);
        assertDoesNotThrow(() -> p.garantirCabe(new BigDecimal("95"), new BigDecimal("5")));
        assertThrows(RegraNegocioVioladaException.class, () -> p.garantirCabe(new BigDecimal("95"), new BigDecimal("5.01")));
    }

    @Test
    void enchePassaACheioEVoltaAAtivoDepoisDaColeta() {
        PostoReciclagem p = posto(StatusPosto.ATIVO);
        p.atualizarLotacao(new BigDecimal("100"));
        assertEquals(StatusPosto.CHEIO, p.getStatus());
        p.liberarAposColeta();
        assertEquals(StatusPosto.ATIVO, p.getStatus());
    }
}
