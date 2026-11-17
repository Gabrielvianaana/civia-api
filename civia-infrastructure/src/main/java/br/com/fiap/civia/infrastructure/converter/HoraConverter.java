package br.com.fiap.civia.infrastructure.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** O Oracle nao possui tipo TIME: horarios sao gravados em VARCHAR2(5) no formato "HH:MM". */
@Converter
public class HoraConverter implements AttributeConverter<LocalTime, String> {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String convertToDatabaseColumn(LocalTime hora) {
        return hora == null ? null : hora.format(FORMATO);
    }

    @Override
    public LocalTime convertToEntityAttribute(String valor) {
        return valor == null ? null : LocalTime.parse(valor.trim(), FORMATO);
    }
}
