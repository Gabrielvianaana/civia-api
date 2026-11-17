package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.SituacaoServico;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * BO que calcula o status exibido no totem para um servico, aplicando as regras de negocio:
 * <ul>
 *   <li><b>RN03</b> - vagas de abrigo sempre em faixa: TEM_VAGA (4+), POUCAS_VAGAS (1 a 3), LOTADO (0).</li>
 *   <li><b>RN04</b> - refeicao, banho e atendimento seguem o horario cadastrado quando nao ha
 *       atualizacao manual recente.</li>
 *   <li><b>RN05</b> - informacao com mais de 2 horas sem atualizacao: SEM_ATUALIZACAO_RECENTE
 *       (vai para o fim da lista). Para refeicao/banho/atendimento, a atualizacao vencida e
 *       descartada e vale o horario cadastrado (RN04), que e uma informacao sempre valida.</li>
 * </ul>
 */
@Component
public class DisponibilidadeBO {

    public static final Duration VALIDADE_ATUALIZACAO = Duration.ofHours(2);
    public static final int MINIMO_VAGAS_TEM_VAGA = 4;

    public Disponibilidade calcular(Servico servico, Optional<AtualizacaoDisponibilidade> ultimaAtualizacao,
                                    LocalDateTime agora) {
        LocalDateTime limite = agora.minus(VALIDADE_ATUALIZACAO);
        Optional<AtualizacaoDisponibilidade> recente = ultimaAtualizacao.filter(a -> !a.ehAnteriorA(limite));

        if (servico.ehAbrigo()) {
            return recente
                    .map(a -> manual(faixaDeVagas(a.getVagas()), a))
                    .orElseGet(DisponibilidadeBO::semAtualizacaoRecente);
        }
        return recente
                .filter(a -> a.getSituacao() != null)
                .map(a -> manual(StatusDisponibilidade.de(a.getSituacao()), servico.getTipo(), a))
                .orElseGet(() -> pelaGradeDeHorario(servico, agora.toLocalTime()));
    }

    /** RN03: converte a quantidade exata de vagas em faixa. A quantidade nunca sai para o totem. */
    public static StatusDisponibilidade faixaDeVagas(Integer vagas) {
        if (vagas == null || vagas <= 0) {
            return StatusDisponibilidade.LOTADO;
        }
        return vagas >= MINIMO_VAGAS_TEM_VAGA ? StatusDisponibilidade.TEM_VAGA : StatusDisponibilidade.POUCAS_VAGAS;
    }

    private static Disponibilidade manual(StatusDisponibilidade status, AtualizacaoDisponibilidade atualizacao) {
        return new Disponibilidade(status, mensagemAbrigo(status), Disponibilidade.Origem.ATUALIZACAO_MANUAL,
                atualizacao.getDataHora());
    }

    private static Disponibilidade manual(StatusDisponibilidade status, TipoServico tipo,
                                          AtualizacaoDisponibilidade atualizacao) {
        return new Disponibilidade(status, mensagemSituacao(atualizacao.getSituacao(), tipo),
                Disponibilidade.Origem.ATUALIZACAO_MANUAL, atualizacao.getDataHora());
    }

    private static Disponibilidade semAtualizacaoRecente() {
        return new Disponibilidade(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE, "Sem atualizacao recente",
                Disponibilidade.Origem.SEM_ATUALIZACAO_RECENTE, null);
    }

    /** RN04: sem atualizacao manual recente, o status segue o horario cadastrado. */
    private static Disponibilidade pelaGradeDeHorario(Servico servico, LocalTime agora) {
        HorarioFuncionamento horario = servico.getHorario();
        boolean aberto = horario.estaAbertoEm(agora);
        String mensagem;
        if (aberto) {
            mensagem = switch (servico.getTipo()) {
                case REFEICAO -> "Servindo agora";
                case ATENDIMENTO -> horario.funciona24Horas() ? "Aberto 24 horas" : "Aberto ate " + hora(horario.fechamento());
                default -> horario.funciona24Horas() ? "Aberto 24 horas" : "Aberto";
            };
        } else {
            mensagem = "Abre as " + hora(horario.abertura());
        }
        return new Disponibilidade(aberto ? StatusDisponibilidade.ABERTO : StatusDisponibilidade.FECHADO, mensagem,
                Disponibilidade.Origem.HORARIO_CADASTRADO, null);
    }

    private static String mensagemAbrigo(StatusDisponibilidade status) {
        return switch (status) {
            case TEM_VAGA -> "Tem vaga";
            case POUCAS_VAGAS -> "Poucas vagas";
            default -> "Lotado";
        };
    }

    private static String mensagemSituacao(SituacaoServico situacao, TipoServico tipo) {
        return switch (situacao) {
            case ABERTO -> tipo == TipoServico.REFEICAO ? "Servindo agora" : "Aberto";
            case FILA_GRANDE -> "Fila grande";
            case LOTADO -> "Lotado";
            case FECHADO -> tipo == TipoServico.REFEICAO ? "Encerrado" : "Fechado";
        };
    }

    /** Formata a hora como o totem exibe: "18h" ou "18h30". */
    static String hora(LocalTime hora) {
        return hora.getMinute() == 0 ? hora.getHour() + "h" : hora.getHour() + "h" + String.format("%02d", hora.getMinute());
    }
}
