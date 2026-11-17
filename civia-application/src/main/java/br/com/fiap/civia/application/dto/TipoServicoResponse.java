package br.com.fiap.civia.application.dto;

/** {@code abrigo} indica o tipo com regras proprias (vagas em faixa e filtro por perfil). */
public record TipoServicoResponse(Integer id, String nome, String icone, boolean abrigo) {
}
