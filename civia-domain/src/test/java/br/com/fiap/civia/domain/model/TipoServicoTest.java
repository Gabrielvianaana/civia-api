package br.com.fiap.civia.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoServicoTest {

    @Test
    void reconheceAbrigoIgnorandoAcentoEMaiusculas() {
        assertTrue(TipoServico.reconstituir(1, "abrigo", "cama").ehAbrigo());
        assertFalse(Fixtures.REFEICAO.ehAbrigo());
        assertTrue(Fixtures.REFEICAO.temNome("REFEICAO"));
    }
}
