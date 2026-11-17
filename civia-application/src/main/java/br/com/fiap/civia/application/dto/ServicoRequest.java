package br.com.fiap.civia.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Contrato de entrada para cadastrar/atualizar um servico (POST e PUT).
 * tipo: ABRIGO, REFEICAO, BANHO ou ATENDIMENTO. perfis: HOMEM, MULHER, FAMILIA, LGBT, IDOSO, COM_ANIMAL.
 */
public record ServicoRequest(

        @NotBlank(message = "O nome e obrigatorio.")
        @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres.")
        String nome,

        @NotBlank(message = "O tipo e obrigatorio (ABRIGO, REFEICAO, BANHO ou ATENDIMENTO).")
        String tipo,

        @NotBlank(message = "O endereco e obrigatorio.")
        @Size(max = 200, message = "O endereco deve ter no maximo 200 caracteres.")
        String endereco,

        @NotNull(message = "A latitude e obrigatoria.")
        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
        Double latitude,

        @NotNull(message = "A longitude e obrigatoria.")
        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
        Double longitude,

        @NotBlank(message = "A hora de abertura e obrigatoria (HH:mm).")
        @Pattern(regexp = ValidacaoPatterns.HORA, message = "A hora de abertura deve estar no formato HH:mm.")
        String horaAbertura,

        @NotBlank(message = "A hora de fechamento e obrigatoria (HH:mm).")
        @Pattern(regexp = ValidacaoPatterns.HORA, message = "A hora de fechamento deve estar no formato HH:mm.")
        String horaFechamento,

        Set<String> perfis,

        /* Opcional: quando ausente o servico nasce NAO aprovado (RN06 - depende do gestor). */
        Boolean aprovado
) {
}
