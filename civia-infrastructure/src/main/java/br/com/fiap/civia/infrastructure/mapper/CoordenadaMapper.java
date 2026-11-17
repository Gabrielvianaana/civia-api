package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.valueobject.Coordenada;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Latitude/longitude sao gravadas como NUMBER(9,6). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoordenadaMapper {

    public static BigDecimal paraDecimal(double grau) {
        return BigDecimal.valueOf(grau).setScale(6, RoundingMode.HALF_UP);
    }

    public static Coordenada paraDominio(BigDecimal latitude, BigDecimal longitude) {
        return new Coordenada(latitude.doubleValue(), longitude.doubleValue());
    }
}
