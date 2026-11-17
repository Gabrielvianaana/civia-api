package br.com.fiap.civia.domain.exception;

/**
 * Os dados estao bem formados, mas a operacao viola uma regra de negocio da CIVIA
 * (ex.: informar vagas para um servico que nao e abrigo, consultar um totem inativo).
 */
public class RegraNegocioVioladaException extends DomainException {

    public RegraNegocioVioladaException(String message) {
        super(message);
    }
}
