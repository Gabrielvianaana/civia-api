package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.GradeHorario;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static br.com.fiap.civia.application.bo.Dados.AGORA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DisponibilidadeBOTest {

    private final DisponibilidadeBO bo = new DisponibilidadeBO();
    private final Servico abrigo = Dados.servico(1, Dados.ABRIGO, 100);
    private final Servico refeicao = Dados.servico(2, Dados.REFEICAO, 100);
    private final GradeHorario semHorario = new GradeHorario(false, List.of());

    @Nested
    @DisplayName("RN03 - vagas de abrigo em faixas")
    class Rn03 {

        @ParameterizedTest(name = "{0} vagas -> {1}")
        @CsvSource({"0, LOTADO", "1, POUCAS_VAGAS", "3, POUCAS_VAGAS", "4, TEM_VAGA", "80, TEM_VAGA"})
        void converteQuantidadeEmFaixa(int vagas, StatusDisponibilidade esperado) {
            assertEquals(esperado, DisponibilidadeBO.faixaDeVagas(vagas));
        }

        @Test
        void abrigoComAtualizacaoRecenteMostraSoAFaixa() {
            Disponibilidade d = bo.calcular(abrigo, semHorario, Optional.of(Dados.vagas(1, 2, AGORA.minusMinutes(30))), AGORA);
            assertEquals(StatusDisponibilidade.POUCAS_VAGAS, d.status());
            assertEquals("Poucas vagas", d.mensagem());
            assertEquals(Disponibilidade.Origem.ATUALIZACAO_RECENTE, d.origem());
        }
    }

    @Nested
    @DisplayName("RN05 - mais de 2h sem atualizacao")
    class Rn05 {

        @Test
        void abrigoComAtualizacaoDeMaisDe2hFicaSemAtualizacaoRecente() {
            Disponibilidade d = bo.calcular(abrigo, semHorario,
                    Optional.of(Dados.vagas(1, 10, AGORA.minusHours(2).minusMinutes(1))), AGORA);
            assertEquals(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE, d.status());
            assertNull(d.dataReferencia());
        }

        @Test
        void atualizacaoComExatamente2hAindaVale() {
            Disponibilidade d = bo.calcular(abrigo, semHorario, Optional.of(Dados.vagas(1, 10, AGORA.minusHours(2))), AGORA);
            assertEquals(StatusDisponibilidade.TEM_VAGA, d.status());
        }

        @Test
        void abrigoSemNenhumaAtualizacaoFicaSemAtualizacaoRecente() {
            assertEquals(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE,
                    bo.calcular(abrigo, semHorario, Optional.empty(), AGORA).status());
        }

        @Test
        void servicoSemHorarioESemAtualizacaoFicaSemAtualizacaoRecente() {
            assertEquals(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE,
                    bo.calcular(refeicao, semHorario, Optional.empty(), AGORA).status());
        }
    }

    @Nested
    @DisplayName("RN04 - horario cadastrado sem atualizacao manual recente")
    class Rn04 {

        @Test
        void refeicaoDentroDoHorarioEstaServindo() {
            GradeHorario grade = new GradeHorario(false, List.of(Dados.quarta(2, "18:00", "21:00")));
            Disponibilidade d = bo.calcular(refeicao, grade, Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals("Servindo agora", d.mensagem());
            assertEquals(Disponibilidade.Origem.HORARIO_CADASTRADO, d.origem());
        }

        @Test
        void foraDoHorarioMostraAProximaAbertura() {
            GradeHorario grade = new GradeHorario(false, List.of(Dados.quarta(3, "07:00", "12:00"), Dados.quarta(3, "20:00", "22:00")));
            Disponibilidade d = bo.calcular(Dados.servico(3, Dados.BANHO, 100), grade, Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.FECHADO, d.status());
            assertEquals("Abre as 20h", d.mensagem());
        }

        @Test
        void atendimentoMostraAteQuandoFunciona() {
            GradeHorario grade = new GradeHorario(false, List.of(Dados.quarta(4, "08:00", "20:30")));
            Disponibilidade d = bo.calcular(Dados.servico(4, Dados.ATENDIMENTO, 100), grade, Optional.empty(), AGORA);
            assertEquals("Aberto ate 20h30", d.mensagem());
        }

        @Test
        void servico24hEstaSempreAberto() {
            Disponibilidade d = bo.calcular(refeicao, new GradeHorario(true, List.of()), Optional.empty(), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals("Aberto 24 horas", d.mensagem());
        }

        @Test
        void atualizacaoManualRecentePrevaleceSobreOHorario() {
            GradeHorario grade = new GradeHorario(false, List.of(Dados.quarta(2, "18:00", "21:00")));
            Disponibilidade d = bo.calcular(refeicao, grade,
                    Optional.of(Dados.situacao(2, StatusDisponibilidade.LOTADO, AGORA.minusMinutes(15))), AGORA);
            assertEquals(StatusDisponibilidade.LOTADO, d.status());
            assertEquals(AGORA.minusMinutes(15), d.dataReferencia());
        }

        @Test
        void atualizacaoManualVencidaEDescartadaEVoltaAoHorario() {
            GradeHorario grade = new GradeHorario(false, List.of(Dados.quarta(2, "18:00", "21:00")));
            Disponibilidade d = bo.calcular(refeicao, grade,
                    Optional.of(Dados.situacao(2, StatusDisponibilidade.FECHADO, AGORA.minusHours(3))), AGORA);
            assertEquals(StatusDisponibilidade.ABERTO, d.status());
            assertEquals(Disponibilidade.Origem.HORARIO_CADASTRADO, d.origem());
        }
    }
}
