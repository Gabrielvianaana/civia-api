package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import static br.com.fiap.civia.domain.model.Fixtures.AGORA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultaTotemTest {

    @Test
    void consultaSemImpressaoNaoTemCodigo() {
        ConsultaTotem c = ConsultaTotem.registrar(1L, 1, 2, null, AGORA, false, true, "ABCD2345");
        assertNull(c.getCodigoChegada());
        assertThrows(RegraNegocioVioladaException.class, () -> c.registrarChegada(AGORA.plusHours(1)));
    }

    @Test
    void imprimirRotaExigeServicoEscolhido() {
        assertThrows(RegraNegocioVioladaException.class,
                () -> ConsultaTotem.registrar(1L, 1, null, null, AGORA, true, false, "ABCD2345"));
    }

    @Test
    void registraChegadaUmaUnicaVez() {
        ConsultaTotem c = ConsultaTotem.registrar(1L, 1, null, 9L, AGORA, true, false, "ABCD2345");
        c.registrarChegada(AGORA.plusMinutes(30));
        assertTrue(c.chegadaRegistrada());
        assertEquals("ABCD2345", c.getCodigoChegada());
        assertThrows(RegraNegocioVioladaException.class, () -> c.registrarChegada(AGORA.plusHours(1)));
    }
}
