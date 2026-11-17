package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entrada de totem. status: ONLINE (padrao), OFFLINE ou MANUTENCAO. */
public record TotemRequest(
        @NotBlank(message = "O codigo do totem e obrigatorio.") @Size(max = 12, message = "O codigo deve ter no maximo 12 caracteres.")
        String codigo,
        @NotBlank(message = "O logradouro e obrigatorio.") @Size(max = 120, message = "O logradouro deve ter no maximo 120 caracteres.")
        String logradouro,
        @NotBlank(message = "O bairro e obrigatorio.") @Size(max = 60, message = "O bairro deve ter no maximo 60 caracteres.")
        String bairro,
        @NotNull(message = "A latitude e obrigatoria.")
        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
        Double latitude,
        @NotNull(message = "A longitude e obrigatoria.")
        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
        Double longitude,
        String status,
        Boolean papelImpressora,
        @PastOrPresent(message = "A data de instalacao nao pode estar no futuro.")
        LocalDate dataInstalacao,
        @PastOrPresent(message = "A ultima sincronizacao nao pode estar no futuro.")
        LocalDateTime ultimaSincronizacao
) {
}
