package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Entrega de material no posto, identificada pelo codigo do cartao anonimo. */
public record EntregaRequest(
        @NotBlank(message = "O codigoCartao e obrigatorio.") @Size(max = 10, message = "O codigoCartao deve ter no maximo 10 caracteres.")
        String codigoCartao,
        @NotNull(message = "O postoId e obrigatorio.") @Positive(message = "O postoId deve ser positivo.")
        Long postoId,
        @NotNull(message = "O materialId e obrigatorio.") @Positive(message = "O materialId deve ser positivo.")
        Integer materialId,
        @NotNull(message = "O pesoKg e obrigatorio.")
        @DecimalMin(value = "0.01", message = "O pesoKg deve ser maior que zero.")
        @DecimalMax(value = "10.00", message = "Cada entrega pode ter no maximo 10 kg.")
        BigDecimal pesoKg
) {
}
