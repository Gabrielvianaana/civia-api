package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Entrada de servico (POST/PUT). statusAprovacao: PENDENTE (padrao no cadastro), APROVADO ou REPROVADO.
 * Os horarios sao cadastrados no sub-recurso /servicos/{id}/horarios.
 */
public record ServicoRequest(
        @NotNull(message = "O organizacaoId e obrigatorio.") @Positive(message = "O organizacaoId deve ser positivo.")
        Long organizacaoId,
        @NotNull(message = "O tipoServicoId e obrigatorio.") @Positive(message = "O tipoServicoId deve ser positivo.")
        Integer tipoServicoId,
        @NotBlank(message = "O nome e obrigatorio.") @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
        String nome,
        @NotBlank(message = "O logradouro e obrigatorio.") @Size(max = 120, message = "O logradouro deve ter no maximo 120 caracteres.")
        String logradouro,
        @Size(max = 10, message = "O numero deve ter no maximo 10 caracteres.")
        String numero,
        @NotBlank(message = "O bairro e obrigatorio.") @Size(max = 60, message = "O bairro deve ter no maximo 60 caracteres.")
        String bairro,
        @NotNull(message = "A latitude e obrigatoria.")
        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
        Double latitude,
        @NotNull(message = "A longitude e obrigatoria.")
        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
        Double longitude,
        @Positive(message = "A capacidade deve ser maior que zero.") @Max(value = 99_999, message = "A capacidade deve ser no maximo 99999.")
        Integer capacidade,
        Boolean funciona24h,
        String statusAprovacao,
        Set<Integer> perfilIds
) {
}
