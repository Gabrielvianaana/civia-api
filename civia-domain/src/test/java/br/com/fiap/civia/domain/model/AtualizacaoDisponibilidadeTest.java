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
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtualizacaoDisponibilidadeTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 20, 0);
    private static final HorarioFuncionamento H = new HorarioFuncionamento(LocalTime.of(8, 0), LocalTime.of(17, 0));

    private static Servico servico(TipoServico tipo) {
        return Servico.reconstituir(1L, "Servico", tipo, "Rua X", new Coordenada(-23.55, -46.63), H,
                Set.of(PerfilAtendido.HOMEM), true, AGORA);
    }

    @Test
    void abrigoRegistraVagas() {
        AtualizacaoDisponibilidade a = AtualizacaoDisponibilidade.registrar(servico(TipoServico.ABRIGO), 5, null, AGORA);
        assertEquals(5, a.getVagas());
        assertEquals(1L, a.getServicoId());
    }

    @Test
    void abrigoSemVagasEhRecusado() {
        assertThrows(RegraNegocioVioladaException.class,
                () -> AtualizacaoDisponibilidade.registrar(servico(TipoServico.ABRIGO), null, null, AGORA));
    }

    @Test
    void abrigoComSituacaoEhRecusado() {
        assertThrows(RegraNegocioVioladaException.class, () -> AtualizacaoDisponibilidade
                .registrar(servico(TipoServico.ABRIGO), 3, SituacaoServico.ABERTO, AGORA));
    }

    @Test
    void vagasNegativasSaoRecusadas() {
        assertThrows(DadoInvalidoException.class,
                () -> AtualizacaoDisponibilidade.registrar(servico(TipoServico.ABRIGO), -1, null, AGORA));
    }

    @Test
    void refeicaoNaoAceitaVagas() {
        assertThrows(RegraNegocioVioladaException.class,
                () -> AtualizacaoDisponibilidade.registrar(servico(TipoServico.REFEICAO), 10, null, AGORA));
    }

    @Test
    void banhoExigeSituacao() {
        assertThrows(RegraNegocioVioladaException.class,
                () -> AtualizacaoDisponibilidade.registrar(servico(TipoServico.BANHO), null, null, AGORA));
        AtualizacaoDisponibilidade a = AtualizacaoDisponibilidade
                .registrar(servico(TipoServico.BANHO), null, SituacaoServico.FILA_GRANDE, AGORA);
        assertEquals(SituacaoServico.FILA_GRANDE, a.getSituacao());
    }
}
