package br.com.fiap.civia.application.dto;

public record HorarioResponse(Long id, Long servicoId, int diaSemana, String nomeDia, String horaAbertura, String horaFechamento) {
}
