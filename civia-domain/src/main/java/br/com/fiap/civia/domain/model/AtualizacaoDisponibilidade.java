package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Registro da disponibilidade de um servico em um instante. Tabela T_ATUALIZACAO.
 * As invariantes espelham os CHECKs do modelo fisico (ck_atu_usuario e ck_atu_faixa),
 * mais as regras que dependem do servico (tipo e capacidade).
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AtualizacaoDisponibilidade {

    public static final int MAXIMO_VAGAS = 99_999;
    private static final Set<StatusDisponibilidade> STATUS_ABRIGO =
            EnumSet.of(StatusDisponibilidade.TEM_VAGA, StatusDisponibilidade.POUCAS_VAGAS, StatusDisponibilidade.LOTADO);

    @EqualsAndHashCode.Include
    private final Long id;
    private Long servicoId;
    private Long usuarioId;
    private StatusDisponibilidade status;
    private Integer vagas;
    private OrigemAtualizacao origem;
    private LocalDateTime dataHora;

    private AtualizacaoDisponibilidade(Long id, Long servicoId, Long usuarioId, StatusDisponibilidade status,
                                       Integer vagas, OrigemAtualizacao origem, LocalDateTime dataHora) {
        this.id = id;
        this.servicoId = Objects.requireNonNull(servicoId, "servicoId e obrigatorio");
        this.usuarioId = usuarioId;
        this.status = status;
        this.vagas = vagas;
        this.origem = origem;
        this.dataHora = Validacoes.obrigatorio(dataHora, "A data/hora da atualizacao e obrigatoria.");
    }

    public static AtualizacaoDisponibilidade registrar(Servico servico, Long usuarioId, StatusDisponibilidade status,
                                                       Integer vagas, OrigemAtualizacao origem, LocalDateTime dataHora) {
        validar(servico, usuarioId, status, vagas, origem);
        return new AtualizacaoDisponibilidade(null, servico.getId(), usuarioId, status, vagas, origem, dataHora);
    }

    public static AtualizacaoDisponibilidade reconstituir(Long id, Long servicoId, Long usuarioId,
                                                          StatusDisponibilidade status, Integer vagas,
                                                          OrigemAtualizacao origem, LocalDateTime dataHora) {
        return new AtualizacaoDisponibilidade(Objects.requireNonNull(id), servicoId, usuarioId, status, vagas, origem,
                dataHora);
    }

    public void corrigir(Servico servico, Long usuarioId, StatusDisponibilidade status, Integer vagas,
                         OrigemAtualizacao origem, LocalDateTime dataHora) {
        validar(servico, usuarioId, status, vagas, origem);
        this.servicoId = servico.getId();
        this.usuarioId = usuarioId;
        this.status = status;
        this.vagas = vagas;
        this.origem = origem;
        this.dataHora = Validacoes.obrigatorio(dataHora, "A data/hora da atualizacao e obrigatoria.");
    }

    public boolean ehAnteriorA(LocalDateTime instante) {
        return dataHora.isBefore(instante);
    }

    private static void validar(Servico servico, Long usuarioId, StatusDisponibilidade status, Integer vagas,
                                OrigemAtualizacao origem) {
        Objects.requireNonNull(servico, "servico e obrigatorio");
        Validacoes.obrigatorio(origem, "A origem da atualizacao e obrigatoria (PAINEL ou CENTRAL_VAGAS).");
        Validacoes.obrigatorio(status, "O status de disponibilidade e obrigatorio.");
        if (status == StatusDisponibilidade.SEM_ATUALIZACAO_RECENTE) {
            throw new DadoInvalidoException("SEM_ATUALIZACAO_RECENTE e calculado pelo sistema e nao pode ser gravado.");
        }
        // ck_atu_usuario
        if (origem == OrigemAtualizacao.PAINEL && usuarioId == null) {
            throw new RegraNegocioVioladaException("Atualizacao pelo PAINEL exige o usuario responsavel.");
        }
        if (origem == OrigemAtualizacao.CENTRAL_VAGAS && usuarioId != null) {
            throw new RegraNegocioVioladaException("Atualizacao da CENTRAL_VAGAS e automatica e nao tem usuario.");
        }
        if (origem == OrigemAtualizacao.CENTRAL_VAGAS && !servico.ehAbrigo()) {
            throw new RegraNegocioVioladaException("A Central de Vagas so informa vagas de abrigo.");
        }
        if (servico.ehAbrigo() && !STATUS_ABRIGO.contains(status)) {
            throw new RegraNegocioVioladaException("Abrigo so aceita os status TEM_VAGA, POUCAS_VAGAS ou LOTADO.");
        }
        // ck_atu_faixa
        if (status.exigeVagas() && vagas == null) {
            throw new RegraNegocioVioladaException("O status " + status + " exige a quantidade de vagas.");
        }
        if (!status.exigeVagas() && vagas != null) {
            throw new RegraNegocioVioladaException("O status " + status + " nao registra quantidade de vagas.");
        }
        if (vagas != null) {
            if (vagas < 0 || vagas > MAXIMO_VAGAS) {
                throw new DadoInvalidoException("A quantidade de vagas deve estar entre 0 e " + MAXIMO_VAGAS + ".");
            }
            boolean faixaCoerente = switch (status) {
                case TEM_VAGA -> vagas >= 4;
                case POUCAS_VAGAS -> vagas >= 1 && vagas <= 3;
                case LOTADO -> vagas == 0;
                default -> false;
            };
            if (!faixaCoerente) {
                throw new RegraNegocioVioladaException("O status " + status + " nao corresponde a " + vagas + " vaga(s).");
            }
            if (servico.getCapacidade() != null && vagas > servico.getCapacidade()) {
                throw new RegraNegocioVioladaException("As vagas informadas (" + vagas
                        + ") excedem a capacidade do servico (" + servico.getCapacidade() + ").");
            }
        }
    }
}
