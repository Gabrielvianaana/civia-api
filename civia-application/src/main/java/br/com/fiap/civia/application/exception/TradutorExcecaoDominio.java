package br.com.fiap.civia.application.exception;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * Anti-corruption entre camadas: a apresentacao nao enxerga o dominio, entao as excecoes
 * de dominio sao convertidas aqui em excecoes de aplicacao.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TradutorExcecaoDominio {

    public static <T> T executar(Supplier<T> acao) {
        try {
            return acao.get();
        } catch (DadoInvalidoException e) {
            throw new DadosInvalidosException(e.getMessage());
        } catch (RegraNegocioVioladaException e) {
            throw new RegraNegocioException(e.getMessage());
        }
    }

    public static void executar(Runnable acao) {
        executar(() -> {
            acao.run();
            return null;
        });
    }
}
