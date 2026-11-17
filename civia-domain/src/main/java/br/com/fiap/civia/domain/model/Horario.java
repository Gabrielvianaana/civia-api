package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Faixa de funcionamento de um servico em um dia da semana. Tabela T_HORARIO.
 * Dia da semana: 1 = domingo ... 7 = sabado. A abertura e sempre anterior ao fechamento
 * (CHECK ck_hor_intervalo); horarios que viram a noite usam duas faixas (ex.: 18:00-23:59 e 00:00-08:00).
 * Fechamento 23:59 vale ate a meia-noite.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Horario {

    public static final LocalTime FIM_DO_DIA = LocalTime.of(23, 59);
    private static final String[] NOMES_DIAS = {"", "domingo", "segunda", "terca", "quarta", "quinta", "sexta", "sabado"};

    @EqualsAndHashCode.Include
    private final Long id;
    private final Long servicoId;
    private int diaSemana;
    private LocalTime abertura;
    private LocalTime fechamento;

    private Horario(Long id, Long servicoId, int diaSemana, LocalTime abertura, LocalTime fechamento) {
        this.id = id;
        this.servicoId = Objects.requireNonNull(servicoId, "servicoId e obrigatorio");
        definir(diaSemana, abertura, fechamento);
    }

    public static Horario novo(Long servicoId, Integer diaSemana, LocalTime abertura, LocalTime fechamento) {
        return new Horario(null, servicoId, diaObrigatorio(diaSemana), abertura, fechamento);
    }

    public static Horario reconstituir(Long id, Long servicoId, int diaSemana, LocalTime abertura, LocalTime fechamento) {
        return new Horario(Objects.requireNonNull(id), servicoId, diaSemana, abertura, fechamento);
    }

    public void atualizar(Integer diaSemana, LocalTime abertura, LocalTime fechamento) {
        definir(diaObrigatorio(diaSemana), abertura, fechamento);
    }

    /** Converte o dia da semana do Java (segunda = 1 ... domingo = 7) para o padrao da tabela (domingo = 1). */
    public static int diaSemanaDe(DayOfWeek dia) {
        return dia.getValue() % 7 + 1;
    }

    public static String nomeDoDia(int diaSemana) {
        return NOMES_DIAS[diaSemana];
    }

    public boolean abrangeInstante(LocalDateTime instante) {
        if (diaSemanaDe(instante.getDayOfWeek()) != diaSemana) {
            return false;
        }
        LocalTime hora = instante.toLocalTime();
        boolean depoisDaAbertura = !hora.isBefore(abertura);
        boolean antesDoFechamento = fechamento.equals(FIM_DO_DIA) || hora.isBefore(fechamento);
        return depoisDaAbertura && antesDoFechamento;
    }

    private void definir(int diaSemana, LocalTime abertura, LocalTime fechamento) {
        if (diaSemana < 1 || diaSemana > 7) {
            throw new DadoInvalidoException("O dia da semana deve estar entre 1 (domingo) e 7 (sabado).");
        }
        Validacoes.obrigatorio(abertura, "A hora de abertura e obrigatoria.");
        Validacoes.obrigatorio(fechamento, "A hora de fechamento e obrigatoria.");
        if (!abertura.isBefore(fechamento)) {
            throw new DadoInvalidoException("A abertura deve ser anterior ao fechamento. Para horarios que viram a noite, "
                    + "cadastre duas faixas (ex.: 18:00-23:59 e 00:00-08:00).");
        }
        this.diaSemana = diaSemana;
        this.abertura = abertura.withSecond(0).withNano(0);
        this.fechamento = fechamento.withSecond(0).withNano(0);
    }

    private static int diaObrigatorio(Integer diaSemana) {
        return Validacoes.obrigatorio(diaSemana, "O dia da semana e obrigatorio (1 = domingo ... 7 = sabado).");
    }
}
