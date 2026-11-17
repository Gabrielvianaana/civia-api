package br.com.fiap.civia.application.exception;

/** A operacao conflita com o estado atual (ex.: identificacao de totem duplicada). Traduzido para HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String message) {
        super(message);
    }
}
