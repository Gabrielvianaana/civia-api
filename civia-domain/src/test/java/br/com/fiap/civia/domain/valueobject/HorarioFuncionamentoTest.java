package br.com.fiap.civia.domain.valueobject;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorarioFuncionamentoTest {

    private static LocalTime t(String hora) {
        return LocalTime.parse(hora);
    }

    @Test
    void horarioDiurno() {
        HorarioFuncionamento h = new HorarioFuncionamento(t("11:00"), t("14:00"));
        assertTrue(h.estaAbertoEm(t("11:00")));
        assertTrue(h.estaAbertoEm(t("13:59")));
        assertFalse(h.estaAbertoEm(t("14:00")));
        assertFalse(h.estaAbertoEm(t("10:59")));
    }

    @Test
    void horarioQueViraANoite() {
        HorarioFuncionamento h = new HorarioFuncionamento(t("18:00"), t("08:00"));
        assertTrue(h.viraANoite());
        assertTrue(h.estaAbertoEm(t("23:30")));
        assertTrue(h.estaAbertoEm(t("02:00")));
        assertFalse(h.estaAbertoEm(t("12:00")));
    }

    @Test
    void aberturaIgualAoFechamentoSignifica24Horas() {
        HorarioFuncionamento h = new HorarioFuncionamento(t("00:00"), t("00:00"));
        assertTrue(h.funciona24Horas());
        assertTrue(h.estaAbertoEm(t("15:00")));
    }

    @Test
    void exigeAberturaEFechamento() {
        assertThrows(DadoInvalidoException.class, () -> new HorarioFuncionamento(null, t("08:00")));
    }
}
