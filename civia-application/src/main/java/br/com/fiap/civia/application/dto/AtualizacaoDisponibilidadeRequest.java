package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

/**
 * Contrato de entrada de uma atualizacao de disponibilidade (POST e PUT).
 * Abrigo: informe {@code vagas}. Refeicao, banho e atendimento: informe {@code situacao}
 * (ABERTO, FILA_GRANDE, LOTADO ou FECHADO). {@code dataHora} e opcional (padrao: agora).
 */
public record AtualizacaoDisponibilidadeRequest(

        @NotNull(message = "O servicoId e obrigatorio.")
        @Positive(message = "O servicoId deve ser positivo.")
        Long servicoId,

        @PositiveOrZero(message = "A quantidade de vagas nao pode ser negativa.")
        @Max(value = 99_999, message = "A quantidade de vagas deve ser no maximo 99999.")
        Integer vagas,

        String situacao,

        LocalDateTime dataHora
) {
}
