package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Alteracao administrativa do cartao. status: ATIVO ou BLOQUEADO (so sem saldo; com saldo, use a substituicao). */
public record CartaoAtualizacaoRequest(
        @NotNull(message = "O organizacaoEmissoraId e obrigatorio.") @Positive(message = "O organizacaoEmissoraId deve ser positivo.")
        Long organizacaoEmissoraId,
        String status
) {
}
