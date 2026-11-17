package br.com.fiap.civia.domain.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Conjunto de horarios de um servico, usado para saber se ele esta aberto em um instante (RN04).
 *
 * @param funciona24h servico 24 horas (fl_24h = 'S') dispensa horarios
 * @param horarios    faixas cadastradas em T_HORARIO
 */
public record GradeHorario(boolean funciona24h, List<Horario> horarios) {

    public GradeHorario {
        horarios = horarios == null ? List.of() : List.copyOf(horarios);
    }

    public boolean estaAbertoEm(LocalDateTime instante) {
        return funciona24h || faixaAtual(instante).isPresent();
    }

    /** Faixa em vigor no instante, se houver (para "Aberto ate 17h"). */
    public Optional<Horario> faixaAtual(LocalDateTime instante) {
        return horarios.stream().filter(h -> h.abrangeInstante(instante)).findFirst();
    }

    /** Proxima abertura ainda no mesmo dia (para "Abre as 18h"). */
    public Optional<Horario> proximaAberturaNoDia(LocalDateTime instante) {
        int dia = Horario.diaSemanaDe(instante.getDayOfWeek());
        return horarios.stream()
                .filter(h -> h.getDiaSemana() == dia && h.getAbertura().isAfter(instante.toLocalTime()))
                .min(Comparator.comparing(Horario::getAbertura));
    }
}
