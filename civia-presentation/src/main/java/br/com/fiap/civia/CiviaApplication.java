package br.com.fiap.civia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Bootstrap da API CIVIA.
 * O fuso America/Sao_Paulo e fixado porque os horarios de funcionamento dos servicos
 * sao cadastrados no horario local de Sao Paulo, e o Render roda em UTC.
 */
@SpringBootApplication
public class CiviaApplication {

    public static final String FUSO_HORARIO = "America/Sao_Paulo";

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(FUSO_HORARIO));
        SpringApplication.run(CiviaApplication.class, args);
    }
}
