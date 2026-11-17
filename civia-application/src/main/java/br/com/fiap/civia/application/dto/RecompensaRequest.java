package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Item do catalogo de recompensas (so itens extras). ativo padrao true. */
public record RecompensaRequest(
        @NotNull(message = "O organizacaoParceiraId e obrigatorio.") @Positive(message = "O organizacaoParceiraId deve ser positivo.")
        Long organizacaoParceiraId,
        @NotBlank(message = "O nome da recompensa e obrigatorio.") @Size(max = 80, message = "O nome deve ter no maximo 80 caracteres.")
        String nome,
        @NotNull(message = "O campo custoCreditos e obrigatorio.")
        @Positive(message = "O campo custoCreditos deve ser positivo.")
        @Max(value = 999999, message = "O campo custoCreditos deve ser no maximo 999999.")
        Integer custoCreditos,
        Boolean ativo
) {
}
