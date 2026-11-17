package br.com.fiap.civia.application.exception;

/** Dados bem formados, mas que violam uma regra de negocio. Traduzido para HTTP 422. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String message) {
        super(message);
    }
}
