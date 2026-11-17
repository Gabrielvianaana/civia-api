package br.com.fiap.civia.domain.exception;

/**
 * Um dado informado viola uma regra de formacao (campo obrigatorio, faixa de valores, formato).
 */
public class DadoInvalidoException extends DomainException {

    public DadoInvalidoException(String message) {
        super(message);
    }
}
