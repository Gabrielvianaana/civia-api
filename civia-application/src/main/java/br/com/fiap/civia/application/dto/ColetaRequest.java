package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Coleta feita pela cooperativa do posto. Sem pesoTotalKg, usa o material acumulado; sem dataColeta, usa agora. */
public record ColetaRequest(
        @NotNull(message = "O postoId e obrigatorio.") @Positive(message = "O postoId deve ser positivo.")
        Long postoId,
        @NotNull(message = "O cooperativaId e obrigatorio.") @Positive(message = "O cooperativaId deve ser positivo.")
        Long cooperativaId,
        @DecimalMin(value = "0.01", message = "O pesoTotalKg deve ser maior que zero.")
        @DecimalMax(value = "99999.99", message = "O pesoTotalKg deve ser no maximo 99999.99.")
        BigDecimal pesoTotalKg,
        @PastOrPresent(message = "A data da coleta nao pode estar no futuro.")
        LocalDateTime dataColeta
) {
}
