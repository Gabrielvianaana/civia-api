package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

/**
 * Card de um servico exibido no totem (RF02/RF03).
 * Nunca traz o numero exato de vagas (RN03): apenas o status em faixa e a mensagem.
 */
public record ServicoProximoResponse(
        Long servicoId,
        String nome,
        String tipo,
        String endereco,
        double latitude,
        double longitude,
        String horaAbertura,
        String horaFechamento,
        long distanciaMetros,
        long minutosCaminhada,
        String status,
        String mensagem,
        String origemInformacao,
        LocalDateTime ultimaAtualizacao,
        Long minutosDesdeAtualizacao
) {
}
