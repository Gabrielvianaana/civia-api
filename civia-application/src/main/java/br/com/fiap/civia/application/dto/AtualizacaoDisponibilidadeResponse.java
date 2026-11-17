package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

/**
 * Representacao de saida de uma atualizacao (visao administrativa do painel).
 * O numero exato de vagas aparece apenas aqui; o totem recebe somente a faixa (RN03).
 */
public record AtualizacaoDisponibilidadeResponse(
        Long id,
        Long servicoId,
        Integer vagas,
        String situacao,
        LocalDateTime dataHora
) {
}
