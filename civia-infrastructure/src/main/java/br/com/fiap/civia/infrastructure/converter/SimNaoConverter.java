package br.com.fiap.civia.infrastructure.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Flags do modelo da equipe sao CHAR(1) com 'S' (sim) ou 'N' (nao). */
@Converter
public class SimNaoConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "S" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String valor) {
        return "S".equalsIgnoreCase(valor == null ? null : valor.trim());
    }
}
