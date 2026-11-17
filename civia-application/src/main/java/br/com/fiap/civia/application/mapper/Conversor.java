package br.com.fiap.civia.application.mapper;

import br.com.fiap.civia.application.exception.DadosInvalidosException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Conversoes de texto da API para tipos do dominio, com mensagens claras (HTTP 400) em caso de valor invalido.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Conversor {

    public static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    /** Converte o texto para o enum; {@code null}/vazio devolve {@code null}. */
    public static <E extends Enum<E>> E paraEnum(Class<E> tipo, String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            String permitidos = Arrays.stream(tipo.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
            throw new DadosInvalidosException(
                    "Valor '" + valor + "' invalido para " + campo + ". Valores aceitos: " + permitidos + ".");
        }
    }

    public static LocalTime paraHora(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " e obrigatorio (formato HH:mm).");
        }
        try {
            return LocalTime.parse(valor.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException("O campo " + campo + " deve estar no formato HH:mm (ex.: 08:00).");
        }
    }

    public static String deHora(LocalTime hora) {
        return hora == null ? null : hora.format(FORMATO_HORA);
    }
}
