package br.com.fiap.civia.application.exception;

/** O recurso solicitado nao existe. Traduzido para HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
