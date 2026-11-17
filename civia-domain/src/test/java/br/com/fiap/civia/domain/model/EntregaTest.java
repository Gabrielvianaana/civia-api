package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntregaTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 10, 0);
    private static final Material LATA = Material.reconstituir(1, "Lata de aluminio", 10, true);

    @Test
    void creditosSaoCalculadosPeloMaterial() {
        Entrega e = Entrega.registrar(1L, 1L, LATA, new BigDecimal("3.5"), AGORA);
        assertEquals(35, e.getCreditosGerados());
        assertEquals(new BigDecimal("3.50"), e.getPesoKg());
    }

    @Test
    void entregaTemNoMaximo10Kg() {
        assertThrows(DadoInvalidoException.class, () -> Entrega.registrar(1L, 1L, LATA, new BigDecimal("10.01"), AGORA));
    }

    @Test
    void materialNaoAceitoERecusado() {
        Material vidro = Material.reconstituir(3, "Vidro", 0, false);
        assertThrows(RegraNegocioVioladaException.class, () -> Entrega.registrar(1L, 1L, vidro, BigDecimal.ONE, AGORA));
    }

    @Test
    void correcaoRecalculaOsCreditos() {
        Entrega e = Entrega.registrar(1L, 1L, LATA, new BigDecimal("2"), AGORA);
        e.corrigir(1L, 1L, Material.reconstituir(2, "Garrafa PET", 5, true), new BigDecimal("2"));
        assertEquals(10, e.getCreditosGerados());
    }
}
