package br.com.fiap.civia.domain.valueobject;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordenadaTest {

    @Test
    void distanciaEntreMesmoPontoEZero() {
        Coordenada se = new Coordenada(-23.550520, -46.633308);
        assertEquals(0, se.distanciaEmMetrosAte(se), 0.001);
    }

    @Test
    void calculaDistanciaPelaFormulaDeHaversine() {
        // 0,01 grau de latitude equivale a ~1.112 metros
        Coordenada origem = new Coordenada(-23.550000, -46.633000);
        Coordenada destino = new Coordenada(-23.560000, -46.633000);
        assertEquals(1112, origem.distanciaEmMetrosAte(destino), 2);
    }

    @Test
    void rejeitaLatitudeForaDaFaixa() {
        assertThrows(DadoInvalidoException.class, () -> new Coordenada(-91, 0));
    }

    @Test
    void rejeitaLongitudeForaDaFaixa() {
        assertThrows(DadoInvalidoException.class, () -> new Coordenada(0, 181));
    }

    @Test
    void rejeitaCoordenadaNula() {
        assertThrows(DadoInvalidoException.class, () -> Coordenada.de(null, -46.6));
    }
}
