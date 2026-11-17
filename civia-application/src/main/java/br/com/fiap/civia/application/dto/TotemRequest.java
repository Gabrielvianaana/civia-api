package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Contrato de entrada para cadastrar/atualizar um totem (POST e PUT). */
public record TotemRequest(

        @NotBlank(message = "A identificacao do totem e obrigatoria.")
        @Size(max = 30, message = "A identificacao deve ter no maximo 30 caracteres.")
        String identificacao,

        @NotBlank(message = "O local de instalacao e obrigatorio.")
        @Size(max = 150, message = "O local deve ter no maximo 150 caracteres.")
        String local,

        @NotNull(message = "A latitude e obrigatoria.")
        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
        Double latitude,

        @NotNull(message = "A longitude e obrigatoria.")
        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
        Double longitude,

        /* Opcional: quando ausente o totem nasce ativo. */
        Boolean ativo
) {
}
