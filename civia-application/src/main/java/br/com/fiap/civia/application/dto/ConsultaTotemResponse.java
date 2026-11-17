package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

public record ConsultaTotemResponse(Long id, Long totemId, Integer tipoServicoId, Integer perfilId, Long servicoEscolhidoId,
                                    LocalDateTime dataConsulta, boolean imprimiuRota, boolean pediuAbordagem,
                                    String codigoChegada, LocalDateTime dataChegada) {
}
