package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.GradeHorario;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * BO que calcula o status exibido no totem para um servico:
 * <ul>
 *   <li><b>RN03</b> - vagas de abrigo sempre em faixa: TEM_VAGA (4+), POUCAS_VAGAS (1 a 3), LOTADO (0).</li>
 *   <li><b>RN04</b> - refeicao, banho e atendimento seguem o horario cadastrado quando nao ha atualizacao
 *       manual mais recente (ate 2 horas).</li>
 *   <li><b>RN05</b> - informacao com mais de 2 horas: SEM_ATUALIZACAO_RECENTE (vai para o fim da lista).
 *       Para os demais tipos, a atualizacao vencida e descartada e vale o horario cadastrado (RN04); sem
 *       horario cadastrado, tambem fica SEM_ATUALIZACAO_RECENTE.</li>
 * </ul>
 */
@Component
public class DisponibilidadeBO {

    public static final Duration VALIDADE_ATUALIZACAO = Duration.ofHours(2);
    public static final int MINIMO_VAGAS_TEM_VAGA = 4;

    public Disponibilidade calcular(Servico servico, GradeHorario grade,
                                    Optional<AtualizacaoDisponibilidade> ultimaAtualizacao, LocalDateTime agora) {
        LocalDateTime limite = agora.minus(VALIDADE_ATUALIZACAO);
        Optional<AtualizacaoDisponibilidade> recente = ultimaAtualizacao.filter(a -> !a.ehAnteriorA(limite));

        if (servico.ehAbrigo()) {
            return recente
                    .map(a -> {
                        StatusDisponibilidade faixa = faixaDeVagas(a.getVagas());
                        return new Disponibilidade(faixa, mensagem(faixa, servico), Disponibilidade.Origem.ATUALIZACAO_RECENTE,
                                a.getDataHora());
                    })
                    .orElseGet(DisponibilidadeBO::semAtualizacaoRecente);
        }
        return recente
                .map(a -> new Disponibilidade(a.getStatus(), mensagem(a.getStatus(), servico),
                        Disponibilidade.Origem.ATUALIZACAO_RECENTE, a.getDataHora()))
                .orElseGet(() -> pelaGradeDeHorario(servico, grade, agora));
    }

    /** RN03: converte a quantidade exata de vagas em faixa. A quantidade nunca sai para o totem. */
    public static StatusDisponibilidade faixaDeVagas(Integer vagas) {
        if (vagas == null || vagas <= 0) {
            return StatusDisponibilidade.LOTADO;
        }
        return vagas >= MINIMO_VAGAS_TEM_VAGA ? StatusDisponibilidade.TEM_VAGA : StatusDisponibilidade.POUCAS_VAGAS;
    }

    private static Disponibilidade semAtualizacaoRecente() {
        return new Disponibilidade(StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE, "Sem atualizacao recente",
                Disponibilidade.Origem.SEM_ATUALIZACAO_RECENTE, null);
    }

    /** RN04: sem atualizacao recente, o status segue o horario cadastrado. */
    private static Disponibilidade pelaGradeDeHorario(Servico servico, GradeHorario grade, LocalDateTime agora) {
        if (!grade.funciona24h() && grade.horarios().isEmpty()) {
            return semAtualizacaoRecente();
        }
        String tipo = tipoNormalizado(servico);
        if (grade.funciona24h()) {
            return horario(StatusDisponibilidade.ABERTO, "Aberto 24 horas");
        }
        Optional<Horario> faixa = grade.faixaAtual(agora);
        if (faixa.isPresent()) {
            String mensagem = switch (tipo) {
                case "REFEICAO" -> "Servindo agora";
                case "ATENDIMENTO" -> "Aberto ate " + hora(faixa.get().getFechamento());
                default -> "Aberto";
            };
            return horario(StatusDisponibilidade.ABERTO, mensagem);
        }
        String mensagem = grade.proximaAberturaNoDia(agora)
                .map(h -> "Abre as " + hora(h.getAbertura()))
                .orElse("REFEICAO".equals(tipo) ? "Encerrado por hoje" : "Fechado agora");
        return horario(StatusDisponibilidade.FECHADO, mensagem);
    }

    private static Disponibilidade horario(StatusDisponibilidade status, String mensagem) {
        return new Disponibilidade(status, mensagem, Disponibilidade.Origem.HORARIO_CADASTRADO, null);
    }

    private static String mensagem(StatusDisponibilidade status, Servico servico) {
        boolean refeicao = "REFEICAO".equals(tipoNormalizado(servico));
        return switch (status) {
            case TEM_VAGA -> "Tem vaga";
            case POUCAS_VAGAS -> "Poucas vagas";
            case LOTADO -> "Lotado";
            case ABERTO -> refeicao ? "Servindo agora" : "Aberto";
            case FILA_GRANDE -> "Fila grande";
            case FECHADO -> refeicao ? "Encerrado" : "Fechado";
            case SEM_ATUALIZACAO_RECENTE -> "Sem atualizacao recente";
        };
    }

    private static String tipoNormalizado(Servico servico) {
        return br.com.fiap.civia.domain.model.TipoServico.normalizar(servico.getTipo().getNome());
    }

    /** Formata a hora como o totem exibe: "18h", "18h30"; 23:59 vira "meia-noite". */
    static String hora(LocalTime hora) {
        if (hora.equals(Horario.FIM_DO_DIA)) {
            return "meia-noite";
        }
        return hora.getMinute() == 0 ? hora.getHour() + "h" : hora.getHour() + "h" + String.format("%02d", hora.getMinute());
    }
}
