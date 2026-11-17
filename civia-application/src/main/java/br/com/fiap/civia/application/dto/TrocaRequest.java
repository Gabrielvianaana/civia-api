package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Troca de creditos por um item do catalogo, registrada por um usuario da equipe (RN11). */
public record TrocaRequest(
        @NotBlank(message = "O codigoCartao e obrigatorio.") @Size(max = 10, message = "O codigoCartao deve ter no maximo 10 caracteres.")
        String codigoCartao,
        @NotNull(message = "O recompensaId e obrigatorio.") @Positive(message = "O recompensaId deve ser positivo.")
        Long recompensaId,
        @NotNull(message = "O usuarioId e obrigatorio.") @Positive(message = "O usuarioId deve ser positivo.")
        Long usuarioId
) {
}
