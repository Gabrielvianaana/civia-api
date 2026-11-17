package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro da disponibilidade de um servico em um instante (painel das equipes, US11 / RF09).
 * Abrigo informa a quantidade de vagas; refeicao, banho e atendimento informam a situacao.
 * Tabela: T_CIVIA_ATUALIZACAO_DISP.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AtualizacaoDisponibilidade {

    public static final int MAXIMO_VAGAS = 99_999;

    @EqualsAndHashCode.Include
    private final Long id;
    private Long servicoId;
    private Integer vagas;
    private SituacaoServico situacao;
    private LocalDateTime dataHora;

    private AtualizacaoDisponibilidade(Long id, Long servicoId, Integer vagas, SituacaoServico situacao,
                                       LocalDateTime dataHora) {
        this.id = id;
        this.servicoId = Objects.requireNonNull(servicoId, "servicoId e obrigatorio");
        this.vagas = vagas;
        this.situacao = situacao;
        this.dataHora = exigirDataHora(dataHora);
    }

    /** Registra uma nova atualizacao, validando o dado conforme o tipo do servico. */
    public static AtualizacaoDisponibilidade registrar(Servico servico, Integer vagas, SituacaoServico situacao,
                                                       LocalDateTime dataHora) {
        validarConformeTipo(servico, vagas, situacao);
        return new AtualizacaoDisponibilidade(null, servico.getId(), vagas, situacao, dataHora);
    }

    public static AtualizacaoDisponibilidade reconstituir(Long id, Long servicoId, Integer vagas,
                                                          SituacaoServico situacao, LocalDateTime dataHora) {
        return new AtualizacaoDisponibilidade(Objects.requireNonNull(id, "id e obrigatorio"), servicoId, vagas,
                situacao, dataHora);
    }

    /** Corrige um registro existente (PUT). */
    public void corrigir(Servico servico, Integer vagas, SituacaoServico situacao, LocalDateTime dataHora) {
        validarConformeTipo(servico, vagas, situacao);
        this.servicoId = servico.getId();
        this.vagas = vagas;
        this.situacao = situacao;
        this.dataHora = exigirDataHora(dataHora);
    }

    public boolean ehAnteriorA(LocalDateTime instante) {
        return dataHora.isBefore(instante);
    }

    private static void validarConformeTipo(Servico servico, Integer vagas, SituacaoServico situacao) {
        Objects.requireNonNull(servico, "servico e obrigatorio");
        if (servico.ehAbrigo()) {
            if (vagas == null) {
                throw new RegraNegocioVioladaException("Para abrigo, informe a quantidade de vagas disponiveis.");
            }
            if (situacao != null) {
                throw new RegraNegocioVioladaException(
                        "Para abrigo, a disponibilidade e informada por vagas, nao por situacao.");
            }
            if (vagas < 0 || vagas > MAXIMO_VAGAS) {
                throw new DadoInvalidoException("A quantidade de vagas deve estar entre 0 e " + MAXIMO_VAGAS + ".");
            }
        } else {
            if (vagas != null) {
                throw new RegraNegocioVioladaException(
                        "Vagas so podem ser informadas para abrigos. Para " + servico.getTipo()
                                + ", informe a situacao (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).");
            }
            if (situacao == null) {
                throw new RegraNegocioVioladaException(
                        "Informe a situacao do servico (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).");
            }
        }
    }

    private static LocalDateTime exigirDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new DadoInvalidoException("A data/hora da atualizacao e obrigatoria.");
        }
        return dataHora;
    }
}
