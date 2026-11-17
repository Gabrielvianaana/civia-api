package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorarioTest {

    private static final LocalDateTime QUARTA_19H = Fixtures.AGORA;

    @Test
    void converteDiaDaSemanaParaOPadraoDaTabela() {
        assertEquals(1, Horario.diaSemanaDe(DayOfWeek.SUNDAY));
        assertEquals(2, Horario.diaSemanaDe(DayOfWeek.MONDAY));
        assertEquals(7, Horario.diaSemanaDe(DayOfWeek.SATURDAY));
    }

    @Test
    void abrangeSomenteODiaEAFaixaCadastrados() {
        Horario quarta = Horario.reconstituir(1L, 1L, 4, LocalTime.of(18, 0), LocalTime.of(21, 0));
        assertTrue(quarta.abrangeInstante(QUARTA_19H));
        assertFalse(quarta.abrangeInstante(QUARTA_19H.withHour(21)));
        assertFalse(quarta.abrangeInstante(QUARTA_19H.plusDays(1)));
    }

    @Test
    void fechamento2359ValeAteAMeiaNoite() {
        Horario h = Horario.reconstituir(1L, 1L, 4, LocalTime.of(18, 0), Horario.FIM_DO_DIA);
        assertTrue(h.abrangeInstante(QUARTA_19H.withHour(23).withMinute(59).withSecond(30)));
    }

    @Test
    void aberturaDeveSerAnteriorAoFechamento() {
        assertThrows(DadoInvalidoException.class, () -> Horario.novo(1L, 2, LocalTime.of(22, 0), LocalTime.of(8, 0)));
        assertThrows(DadoInvalidoException.class, () -> Horario.novo(1L, 8, LocalTime.of(8, 0), LocalTime.of(9, 0)));
    }

    @Test
    void gradeIndicaProximaAberturaDoDia() {
        GradeHorario grade = new GradeHorario(false, List.of(
                Horario.reconstituir(1L, 1L, 4, LocalTime.of(11, 0), LocalTime.of(14, 0)),
                Horario.reconstituir(2L, 1L, 4, LocalTime.of(20, 0), LocalTime.of(22, 0))));
        assertFalse(grade.estaAbertoEm(QUARTA_19H));
        assertEquals(LocalTime.of(20, 0), grade.proximaAberturaNoDia(QUARTA_19H).orElseThrow().getAbertura());
        assertTrue(new GradeHorario(true, List.of()).estaAbertoEm(QUARTA_19H));
    }
}
