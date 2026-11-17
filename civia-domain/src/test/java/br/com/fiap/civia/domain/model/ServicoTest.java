package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicoTest {

    private static final Endereco END = new Endereco("Rua A", null, "Se");
    private static final Coordenada SE = new Coordenada(-23.55, -46.63);

    @Test
    void novoServicoNasceComoPendente() {
        Servico s = Servico.novo(1L, Fixtures.REFEICAO, "Refeitorio", END, SE, null, false, null, Fixtures.AGORA, null);
        assertEquals(StatusAprovacao.PENDENTE, s.getStatusAprovacao());
    }

    @Test
    void abrigoExigePerfilAtendido() {
        assertThrows(RegraNegocioVioladaException.class, () -> Servico.novo(1L, Fixtures.ABRIGO, "Abrigo", END, SE, 10,
                false, null, Fixtures.AGORA, Set.of()));
    }

    @Test
    void capacidadeDeveSerPositiva() {
        assertThrows(DadoInvalidoException.class, () -> Servico.novo(1L, Fixtures.ABRIGO, "Abrigo", END, SE, 0,
                false, null, Fixtures.AGORA, Set.of(Fixtures.MULHER)));
    }

    @Test
    void verificaPerfilPeloId() {
        Servico s = Fixtures.servico(1, Fixtures.ABRIGO, 10, 1);
        assertTrue(s.atendePerfil(PerfilAtendido.reconstituir(2, "Mulher")));
    }
}
