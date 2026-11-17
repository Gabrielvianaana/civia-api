package br.com.fiap.civia.application.dto;

import java.math.BigDecimal;

/** materialAcumuladoKg: peso entregue no posto desde a ultima coleta. */
public record PostoReciclagemResponse(Long id, Long totemId, Long cooperativaId, BigDecimal capacidadeKg,
                                      BigDecimal limiteDiarioKg, String status, BigDecimal materialAcumuladoKg) {
}
