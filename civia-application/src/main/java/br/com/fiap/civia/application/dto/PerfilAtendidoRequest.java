package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilAtendidoRequest(
        @NotBlank(message = "A descricao e obrigatoria.") @Size(max = 40, message = "A descricao deve ter no maximo 40 caracteres.")
        String descricao
) {
}
