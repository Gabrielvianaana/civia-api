package br.com.fiap.civia.presentation.config;

import br.com.fiap.civia.CiviaApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Relogio unico da aplicacao, no fuso de Sao Paulo. Injetado nos BOs para que as regras de horario
 * (RN04/RN05) sejam deterministicas e testaveis com Clock.fixed.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(CiviaApplication.FUSO_HORARIO));
    }
}
