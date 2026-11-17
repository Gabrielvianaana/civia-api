package br.com.fiap.civia.presentation.config;

import br.com.fiap.civia.application.bo.CargaInicialBO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Dispara a carga de dados de demonstracao na subida da aplicacao (somente com as tabelas vazias).
 * Pode ser desligada com civia.carga-inicial.habilitada=false.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "civia.carga-inicial.habilitada", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final CargaInicialBO cargaInicialBO;

    @Override
    public void run(String... args) {
        cargaInicialBO.carregarSeNecessario();
    }
}
