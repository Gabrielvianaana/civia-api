package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Entrada de material. Material aceito (RN09) gera creditos por kg; nao aceito tem 0 creditos. */
public record MaterialRequest(
        @NotBlank(message = "O nome do material e obrigatorio.") @Size(max = 40, message = "O nome deve ter no maximo 40 caracteres.")
        String nome,
        @NotNull(message = "O campo creditosPorKg e obrigatorio.")
        @PositiveOrZero(message = "O campo creditosPorKg nao pode ser negativo.")
        @Max(value = 99999, message = "O campo creditosPorKg deve ser no maximo 99999.")
        Integer creditosPorKg,
        @NotNull(message = "O campo aceito e obrigatorio.")
        Boolean aceito
) {
}
