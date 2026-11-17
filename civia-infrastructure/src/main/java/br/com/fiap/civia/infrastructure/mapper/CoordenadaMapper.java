package br.com.fiap.civia.infrastructure.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Latitude/longitude sao gravadas como NUMBER(9,6) (precisao de ~10 cm). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoordenadaMapper {

    public static BigDecimal paraDecimal(double grau) {
        return BigDecimal.valueOf(grau).setScale(6, RoundingMode.HALF_UP);
    }
}
