package br.com.fiap.civia.application.dto;

/** Expressoes regulares compartilhadas pelas validacoes dos DTOs. */
public final class ValidacaoPatterns {

    /** Hora no formato 24h HH:mm (00:00 a 23:59). */
    public static final String HORA = "^([01]\\d|2[0-3]):[0-5]\\d$";

    private ValidacaoPatterns() {
    }
}
