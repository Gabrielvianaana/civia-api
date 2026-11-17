package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/** Validacoes de formacao compartilhadas pelas entidades do dominio. */
public final class Validacoes {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validacoes() {
    }

    /** Texto obrigatorio, sem espacos nas pontas e com tamanho maximo (igual ao VARCHAR2 da tabela). */
    public static String textoObrigatorio(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("O campo " + campo + " e obrigatorio.");
        }
        return limitar(valor.trim(), campo, tamanhoMaximo);
    }

    /** Texto opcional: vazio vira nulo. */
    public static String textoOpcional(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return limitar(valor.trim(), campo, tamanhoMaximo);
    }

    public static String emailOpcional(String valor, String campo) {
        String email = textoOpcional(valor, campo, 120);
        if (email != null && !EMAIL.matcher(email).matches()) {
            throw new DadoInvalidoException("O campo " + campo + " deve ser um e-mail valido.");
        }
        return email == null ? null : email.toLowerCase();
    }

    public static <T> T obrigatorio(T valor, String mensagem) {
        if (valor == null) {
            throw new DadoInvalidoException(mensagem);
        }
        return valor;
    }

    /** Decimal obrigatorio, maior que zero, com no maximo 2 casas (NUMBER(p,2)) e ate {@code maximo}. */
    public static BigDecimal decimalPositivo(BigDecimal valor, String campo, BigDecimal maximo) {
        if (valor == null) {
            throw new DadoInvalidoException("O campo " + campo + " e obrigatorio.");
        }
        BigDecimal ajustado = valor.setScale(2, RoundingMode.HALF_UP);
        if (ajustado.signum() <= 0 || ajustado.compareTo(maximo) > 0) {
            throw new DadoInvalidoException("O campo " + campo + " deve ser maior que 0 e no maximo " + maximo + ".");
        }
        return ajustado;
    }

    private static String limitar(String valor, String campo, int tamanhoMaximo) {
        if (valor.length() > tamanhoMaximo) {
            throw new DadoInvalidoException("O campo " + campo + " deve ter no maximo " + tamanhoMaximo + " caracteres.");
        }
        return valor;
    }
}
