package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Registro de uma consulta no totem (sem dado pessoal - RNF07). Ao imprimir a rota, o sistema gera o
 * codigo de chegada de 8 caracteres que sai no papel.
 */
public record ConsultaTotemRequest(
        @NotNull(message = "O totemId e obrigatorio.") @Positive(message = "O totemId deve ser positivo.")
        Long totemId,
        @NotNull(message = "O tipoServicoId e obrigatorio.") @Positive(message = "O tipoServicoId deve ser positivo.")
        Integer tipoServicoId,
        @Positive(message = "O perfilId deve ser positivo.")
        Integer perfilId,
        @Positive(message = "O servicoEscolhidoId deve ser positivo.")
        Long servicoEscolhidoId,
        Boolean imprimiuRota,
        Boolean pediuAbordagem
) {
}
