package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada de tipo de servico (tela inicial do totem). */
public record TipoServicoRequest(
        @NotBlank(message = "O nome e obrigatorio.") @Size(max = 40, message = "O nome deve ter no maximo 40 caracteres.")
        String nome,
        @NotBlank(message = "O icone e obrigatorio.") @Size(max = 30, message = "O icone deve ter no maximo 30 caracteres.")
        String icone
) {
}
