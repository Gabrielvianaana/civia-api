package br.com.fiap.civia.application.dto;

public record UsuarioPainelResponse(Long id, Long organizacaoId, String nome, String email, String perfilAcesso, boolean ativo) {
}
