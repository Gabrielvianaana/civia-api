package br.com.fiap.civia.application.exception;

/** Dado de entrada mal formado ou fora da faixa permitida. Traduzido para HTTP 400. */
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String message) {
        super(message);
    }
}
