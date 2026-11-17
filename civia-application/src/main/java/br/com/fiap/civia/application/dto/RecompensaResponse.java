package br.com.fiap.civia.application.dto;

public record RecompensaResponse(Long id, Long organizacaoParceiraId, String nome, int custoCreditos, boolean ativo) {
}
