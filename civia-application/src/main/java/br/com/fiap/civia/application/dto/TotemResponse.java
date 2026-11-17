package br.com.fiap.civia.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TotemResponse(Long id, String codigo, String logradouro, String bairro, double latitude, double longitude,
                            String status, boolean papelImpressora, LocalDate dataInstalacao,
                            LocalDateTime ultimaSincronizacao) {
}
