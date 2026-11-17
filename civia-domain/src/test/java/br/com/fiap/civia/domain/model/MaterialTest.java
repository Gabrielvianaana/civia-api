package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** RN09 e ck_mat_aceito: aceito gera creditos; nao aceito tem 0 e e recusado no posto. */
class MaterialTest {

    @Test
    void materialAceitoCalculaCreditosPeloPeso() {
        Material lata = Material.reconstituir(1, "Lata de aluminio", 10, true);
        assertEquals(25, lata.calcularCreditos(new BigDecimal("2.50")));
        assertEquals(13, Material.reconstituir(2, "Garrafa PET", 5, true).calcularCreditos(new BigDecimal("2.50")));
        assertDoesNotThrow(lata::garantirAceito);
    }

    @Test
    void vidroNaoEAceito() {
        Material vidro = Material.reconstituir(3, "Vidro", 0, false);
        assertThrows(RegraNegocioVioladaException.class, vidro::garantirAceito);
    }

    @Test
    void creditosDevemCombinarComAceite() {
        assertThrows(DadoInvalidoException.class, () -> Material.novo("Lata", 0, true));
        assertThrows(DadoInvalidoException.class, () -> Material.novo("Vidro", 3, false));
    }
}
