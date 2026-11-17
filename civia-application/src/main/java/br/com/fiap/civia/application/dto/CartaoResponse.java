package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

public record CartaoResponse(Long id, String codigo, Long organizacaoEmissoraId, Long cartaoAnteriorId,
                             LocalDateTime dataEmissao, String status, int saldoCreditos) {
}
