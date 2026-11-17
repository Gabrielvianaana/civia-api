package br.com.fiap.civia.domain.valueobject;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;

/**
 * Value Object de posicao geografica (graus decimais, WGS84).
 * Imutavel e auto-validado: latitude entre -90 e 90, longitude entre -180 e 180.
 */
public record Coordenada(double latitude, double longitude) {

    private static final double RAIO_TERRA_METROS = 6_371_000d;

    public Coordenada {
        if (Double.isNaN(latitude) || latitude < -90 || latitude > 90) {
            throw new DadoInvalidoException("A latitude deve estar entre -90 e 90.");
        }
        if (Double.isNaN(longitude) || longitude < -180 || longitude > 180) {
            throw new DadoInvalidoException("A longitude deve estar entre -180 e 180.");
        }
    }

    public static Coordenada de(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new DadoInvalidoException("Latitude e longitude sao obrigatorias.");
        }
        return new Coordenada(latitude, longitude);
    }

    /** Distancia em linha reta (formula de Haversine), em metros. */
    public double distanciaEmMetrosAte(Coordenada destino) {
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(destino.latitude);
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(destino.longitude - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RAIO_TERRA_METROS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
