package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;

/** Visao administrativa (painel): traz a quantidade de vagas. O totem recebe so a faixa (RN03). */
public record AtualizacaoDisponibilidadeResponse(
        Long id, Long servicoId, Long usuarioId, String status, Integer vagas, String origem, LocalDateTime dataHora) {
}
