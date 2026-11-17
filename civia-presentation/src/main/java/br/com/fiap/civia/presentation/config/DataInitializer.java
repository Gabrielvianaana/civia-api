package br.com.fiap.civia.presentation.config;

import br.com.fiap.civia.application.bo.CargaInicialBO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Na subida da aplicacao, garante as tabelas de dominio (tipos de servico e perfis) quando vazias.
 * Pode ser desligado com civia.carga-inicial.habilitada=false.
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
