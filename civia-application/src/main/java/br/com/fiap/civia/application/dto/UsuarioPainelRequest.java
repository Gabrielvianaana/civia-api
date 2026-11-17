package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Entrada de usuario do painel. perfilAcesso: GESTOR, EQUIPE ou COOPERATIVA. */
public record UsuarioPainelRequest(
        @NotNull(message = "O organizacaoId e obrigatorio.") @Positive(message = "O organizacaoId deve ser positivo.")
        Long organizacaoId,
        @NotBlank(message = "O nome e obrigatorio.") @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres.")
        String nome,
        @NotBlank(message = "O e-mail e obrigatorio.") @Email(message = "O e-mail informado e invalido.")
        @Size(max = 120, message = "O e-mail deve ter no maximo 120 caracteres.")
        String email,
        @NotBlank(message = "O perfilAcesso e obrigatorio (GESTOR, EQUIPE ou COOPERATIVA).")
        String perfilAcesso,
        Boolean ativo
) {
}
