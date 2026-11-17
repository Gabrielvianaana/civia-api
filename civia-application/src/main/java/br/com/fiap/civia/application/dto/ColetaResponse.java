package br.com.fiap.civia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ColetaResponse(Long id, Long postoId, Long cooperativaId, BigDecimal pesoTotalKg, LocalDateTime dataColeta) {
}
