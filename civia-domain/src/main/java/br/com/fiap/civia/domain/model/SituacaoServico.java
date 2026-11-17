package br.com.fiap.civia.domain.model;

/**
 * Situacao informada manualmente pelas equipes para refeicao, banho e atendimento
 * (botoes de um toque do painel, US11). Abrigos informam vagas em vez de situacao.
 */
public enum SituacaoServico {
    ABERTO,
    FILA_GRANDE,
    LOTADO,
    FECHADO
}
