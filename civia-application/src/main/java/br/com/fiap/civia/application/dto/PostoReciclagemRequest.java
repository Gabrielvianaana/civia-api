package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Entrada de posto de reciclagem. limiteDiarioKg padrao 10 (RN08); status: ATIVO (padrao), CHEIO ou MANUTENCAO. */
public record PostoReciclagemRequest(
        @NotNull(message = "O totemId e obrigatorio.") @Positive(message = "O totemId deve ser positivo.")
        Long totemId,
        @NotNull(message = "O cooperativaId e obrigatorio.") @Positive(message = "O cooperativaId deve ser positivo.")
        Long cooperativaId,
        @NotNull(message = "A capacidadeKg e obrigatoria.")
        @DecimalMin(value = "0.01", message = "A capacidadeKg deve ser maior que zero.")
        @DecimalMax(value = "9999.99", message = "A capacidadeKg deve ser no maximo 9999.99.")
        BigDecimal capacidadeKg,
        @DecimalMin(value = "0.01", message = "O limiteDiarioKg deve ser maior que zero.")
        @DecimalMax(value = "999.99", message = "O limiteDiarioKg deve ser no maximo 999.99.")
        BigDecimal limiteDiarioKg,
        String status
) {
}
