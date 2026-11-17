package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

public record TrocaResponse(Long id, Long cartaoId, Long recompensaId, Long usuarioId, int creditosUsados,
                            LocalDateTime dataTroca) {
}
