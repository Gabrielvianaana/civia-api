package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

/** Card de um servico no totem (RF02/RF03). Nunca traz o numero de vagas (RN03): so a faixa e a mensagem. */
public record ServicoProximoResponse(
        Long servicoId,
        String nome,
        String tipoServico,
        String endereco,
        double latitude,
        double longitude,
        long distanciaMetros,
        long minutosCaminhada,
        String status,
        String mensagem,
        String origemInformacao,
        LocalDateTime ultimaAtualizacao,
        Long minutosDesdeAtualizacao
) {
}
