package br.com.fiap.civia.application.dto;

/** Representacao de saida do recurso totem. */
public record TotemResponse(
        Long id,
        String identificacao,
        String local,
        double latitude,
        double longitude,
        boolean ativo
) {
}
