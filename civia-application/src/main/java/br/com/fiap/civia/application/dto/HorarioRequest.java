package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Faixa de funcionamento. diaSemana: 1 = domingo ... 7 = sabado. Abertura anterior ao fechamento. */
public record HorarioRequest(
        @NotNull(message = "O diaSemana e obrigatorio (1 = domingo ... 7 = sabado).")
        @Min(value = 1, message = "O diaSemana deve estar entre 1 e 7.") @Max(value = 7, message = "O diaSemana deve estar entre 1 e 7.")
        Integer diaSemana,
        @NotBlank(message = "A hora de abertura e obrigatoria (HH:mm).")
        @Pattern(regexp = ValidacaoPatterns.HORA, message = "A hora de abertura deve estar no formato HH:mm.")
        String horaAbertura,
        @NotBlank(message = "A hora de fechamento e obrigatoria (HH:mm).")
        @Pattern(regexp = ValidacaoPatterns.HORA, message = "A hora de fechamento deve estar no formato HH:mm.")
        String horaFechamento
) {
}
