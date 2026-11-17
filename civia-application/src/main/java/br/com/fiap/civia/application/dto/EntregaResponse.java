package br.com.fiap.civia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EntregaResponse(Long id, Long cartaoId, Long postoId, Integer materialId, BigDecimal pesoKg,
                              int creditosGerados, LocalDateTime dataEntrega) {
}
