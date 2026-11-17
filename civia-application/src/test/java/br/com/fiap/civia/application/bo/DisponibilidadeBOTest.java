package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.SituacaoServico;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DisponibilidadeBOTest {

    /** Quarta-feira, 19h00 (horario de Sao Paulo). */
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 19, 0);

    private final DisponibilidadeBO bo = new DisponibilidadeBO();

    private static Servico servico(TipoServico tipo, String abertura, String fechamento) {
        return Servico.reconstituir(1L, "Servico " + tipo, tipo, "Rua X, 10", new Coordenada(-23.55, -46.63),
                new HorarioFuncionamento(LocalTime.parse(abertura), LocalTime.parse(fechamento)),
                Set.of(PerfilAtendido.HOMEM), true, AGORA.minusDays(10));
    }

    private static Optional<AtualizacaoDisponibilidade> vagas(int vagas, LocalDateTime quando) {
        return Optional.of(AtualizacaoDisponibilidade.reconstituir(10L, 1L, vagas, null, quando));
    }

    private static Optional<AtualizacaoDisponibilidade> situacao(SituacaoServico s, LocalDateTime quando) {
        return Optional.of(AtualizacaoDisponibilidade.reconstituir(10L, 1L, null, s, quando));
    }

    @Nested
    @DisplayName("RN03 - vagas de abrigo em faixas")
    class Rn03 {

        @ParameterizedTest(name = "{0} vagas -> {1}")
        @CsvSource({"0, LOTADO", "1, POUCAS_VAGAS", "3, POUCAS_VAGAS", "4, TEM_VAGA", "50, TEM_VAGA"})
        void converteQuantidadeEmFaixa(int vagas, StatusDisponibilidade esperado) {
            assertEquals(esperado, DisponibilidadeBO.faixaDeVagas(vagas));
        }

        @Test
        void abrigoComAtualizacaoRecenteMostraFaixaENuncaONumero() {
            Disponibilidade d = bo.calcular(servico(TipoServico.ABRIGO, "18:00", "08:00"),
                    vagas(2, AGORA.minusMinutes(30)), AGORA);
            assertEquals(StatusDisponibilidade.POUCAS_VAGAS, d.status());
            assertEquals("Poucas vagas", d.mensagem());
            assertEquals(Disponibilidade.Origem.ATUALIZACAO_MANUAL, d.origem());
        }
    }

    @Nested
    @DisplayName("RN05 - mais de 2h sem atualizacao")
    class Rn05 {

        @Test
        void abrigoComAtualizacaoDeMaisDe2hFicaSemAtualizacaoRecente() {
            Disponibilidade d = bo.calcular(servico(TipoServico.ABRIGO, "18:00", "08:00"),
                    vagas(10, AGORA.minusHours(2).minusMinutes(1)), AGORA);
            assertEquals(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE, d.status());
            assertEquals("Sem atualizacao recente", d.mensagem());
            assertNull(d.dataReferencia());
        }

        @Test
        void atualizacaoComExatamente2hAindaVale() {
            Disponibilidade d = bo.calcular(servico(TipoServico.ABRIGO, "18:00", "08:00"),
                    vagas(10, AGORA.minusHours(2)), AGORA);
            assertEquals(StatusDisponibilidade.TEM_VAGA, d.status());
        }

        @Test
        void abrigoSemNenhumaAtualizacaoFicaSemAtualizacaoRecente() {
            Disponibilidade d = bo.calcular(servico(TipoServico.ABRIGO, "18:00", "08:00"), Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE, d.status());
        }

        @Test
        void semAtualizacaoRecenteVaiParaOFimDaLista() {
            assertEquals(2, StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE.getGrupoOrdenacao());
            assertEquals(1, StatusDisponibilidade.LOTADO.getGrupoOrdenacao());
            assertEquals(0, StatusDisponibilidade.TEM_VAGA.getGrupoOrdenacao());
        }
    }

    @Nested
    @DisplayName("RN04 - horario cadastrado sem atualizacao manual recente")
    class Rn04 {

        @Test
        void refeicaoSemAtualizacaoDentroDoHorarioEstaServindo() {
            Disponibilidade d = bo.calcular(servico(TipoServico.REFEICAO, "18:00", "20:00"), Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals("Servindo agora", d.mensagem());
            assertEquals(Disponibilidade.Origem.HORARIO_CADASTRADO, d.origem());
        }

        @Test
        void banhoForaDoHorarioMostraQuandoAbre() {
            Disponibilidade d = bo.calcular(servico(TipoServico.BANHO, "07:00", "17:00"), Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.FECHADO, d.status());
            assertEquals("Abre as 7h", d.mensagem());
        }

        @Test
        void atendimentoAbertoMostraAteQuandoFunciona() {
            Disponibilidade d = bo.calcular(servico(TipoServico.ATENDIMENTO, "08:00", "20:30"), Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals("Aberto ate 20h30", d.mensagem());
        }

        @Test
        void atualizacaoManualRecentePrevaleceSobreOHorario() {
            Disponibilidade d = bo.calcular(servico(TipoServico.REFEICAO, "18:00", "20:00"),
                    situacao(SituacaoServico.LOTADO, AGORA.minusMinutes(15)), AGORA);
            assertEquals(StatusDisponibilidade.LOTADO, d.status());
            assertEquals(Disponibilidade.Origem.ATUALIZACAO_MANUAL, d.origem());
            assertEquals(AGORA.minusMinutes(15), d.dataReferencia());
        }

        @Test
        void atualizacaoManualVencidaEDescartadaEVoltaAoHorario() {
            Disponibilidade d = bo.calcular(servico(TipoServico.REFEICAO, "18:00", "20:00"),
                    situacao(SituacaoServico.FECHADO, AGORA.minusHours(3)), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals(Disponibilidade.Origem.HORARIO_CADASTRADO, d.origem());
        }

        @Test
        void refeicaoFechadaManualmenteMostraEncerrado() {
            Disponibilidade d = bo.calcular(servico(TipoServico.REFEICAO, "18:00", "20:00"),
                    situacao(SituacaoServico.FECHADO, AGORA.minusMinutes(5)), AGORA);
            assertEquals(StatusDisponibilidade.FECHADO, d.status());
            assertEquals("Encerrado", d.mensagem());
        }
    }
}
