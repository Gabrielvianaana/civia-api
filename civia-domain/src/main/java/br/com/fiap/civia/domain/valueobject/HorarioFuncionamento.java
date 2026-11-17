package br.com.fiap.civia.domain.valueobject;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;

import java.time.LocalTime;

/**
 * Value Object do horario de funcionamento diario de um servico.
 * Suporta horarios que viram a noite (ex.: abrigo 18:00 as 08:00).
 * Abertura igual ao fechamento significa funcionamento 24 horas.
 */
public record HorarioFuncionamento(LocalTime abertura, LocalTime fechamento) {

    public HorarioFuncionamento {
        if (abertura == null || fechamento == null) {
            throw new DadoInvalidoException("Os horarios de abertura e de fechamento sao obrigatorios.");
        }
    }

    public boolean funciona24Horas() {
        return abertura.equals(fechamento);
    }

    public boolean viraANoite() {
        return fechamento.isBefore(abertura);
    }

    public boolean estaAbertoEm(LocalTime hora) {
        if (funciona24Horas()) {
            return true;
        }
        if (viraANoite()) {
            return !hora.isBefore(abertura) || hora.isBefore(fechamento);
        }
        return !hora.isBefore(abertura) && hora.isBefore(fechamento);
    }
}
