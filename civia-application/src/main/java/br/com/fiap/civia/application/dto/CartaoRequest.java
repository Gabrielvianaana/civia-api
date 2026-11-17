package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Emissao de cartao pelo Centro POP (o codigo e gerado pelo sistema; sem nome, CPF ou foto - RN07).
 * Com cartaoAnteriorId e a substituicao de um cartao perdido: o anterior e bloqueado e o saldo passa para o novo (RN12).
 */
public record CartaoRequest(
        @NotNull(message = "O organizacaoEmissoraId e obrigatorio.") @Positive(message = "O organizacaoEmissoraId deve ser positivo.")
        Long organizacaoEmissoraId,
        @Positive(message = "O cartaoAnteriorId deve ser positivo.")
        Long cartaoAnteriorId
) {
}
