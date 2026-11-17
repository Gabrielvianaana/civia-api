package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicoTest {

    private static final Coordenada SE = new Coordenada(-23.55, -46.63);
    private static final HorarioFuncionamento NOITE = new HorarioFuncionamento(LocalTime.of(18, 0), LocalTime.of(8, 0));

    @Test
    void abrigoExigePerfilAtendido() {
        assertThrows(RegraNegocioVioladaException.class, () -> Servico.novo("Abrigo", TipoServico.ABRIGO,
                "Rua A, 1", SE, NOITE, Set.of(), true, LocalDateTime.now()));
    }

    @Test
    void refeicaoNaoExigePerfil() {
        Servico s = Servico.novo("Refeitorio", TipoServico.REFEICAO, "Rua B, 2", SE, NOITE, null, false,
                LocalDateTime.now());
        assertTrue(s.getPerfis().isEmpty());
        assertFalse(s.isAprovado());
    }

    @Test
    void verificaPerfilAtendido() {
        Servico s = Servico.novo("Abrigo", TipoServico.ABRIGO, "Rua A, 1", SE, NOITE,
                Set.of(PerfilAtendido.MULHER), true, LocalDateTime.now());
        assertTrue(s.atendePerfil(PerfilAtendido.MULHER));
        assertFalse(s.atendePerfil(PerfilAtendido.HOMEM));
    }

    @Test
    void removeEspacosENaoAceitaNomeVazio() {
        Servico s = Servico.novo("  Banho  ", TipoServico.BANHO, "Rua C", SE, NOITE, null, true, LocalDateTime.now());
        assertEquals("Banho", s.getNome());
        assertThrows(DadoInvalidoException.class, () -> Servico.novo(" ", TipoServico.BANHO, "Rua C", SE, NOITE,
                null, true, LocalDateTime.now()));
    }
}
