package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada de organizacao. tipo: CENTRO_POP, ONG, COOPERATIVA, PREFEITURA ou PARCEIRO. */
public record OrganizacaoRequest(
        @NotBlank(message = "O nome e obrigatorio.") @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
        String nome,
        @NotBlank(message = "O tipo e obrigatorio (CENTRO_POP, ONG, COOPERATIVA, PREFEITURA ou PARCEIRO).")
        String tipo,
        @Size(max = 15, message = "O telefone deve ter no maximo 15 caracteres.")
        String telefone,
        @Email(message = "O e-mail informado e invalido.") @Size(max = 120, message = "O e-mail deve ter no maximo 120 caracteres.")
        String email
) {
}
