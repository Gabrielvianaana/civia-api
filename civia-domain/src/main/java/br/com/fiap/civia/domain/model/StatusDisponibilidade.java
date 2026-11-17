package br.com.fiap.civia.domain.model;

/**
 * Status de disponibilidade (T_ATUALIZACAO.st_disponibilidade) e status exibido no totem.
 * SEM_ATUALIZACAO_RECENTE nao e gravado: e calculado na consulta (RN05).
 * O grupo de ordenacao define a posicao na lista: disponiveis, depois lotados/fechados (US02)
 * e por ultimo sem atualizacao recente (RN05).
 */
public enum StatusDisponibilidade {

    TEM_VAGA(0, true),
    POUCAS_VAGAS(0, true),
    ABERTO(0, false),
    FILA_GRANDE(0, false),
    LOTADO(1, true),
    FECHADO(1, false),
    SEM_ATUALIZACAO_RECENTE(2, false);

    private final int grupoOrdenacao;
    private final boolean exigeVagas;

    StatusDisponibilidade(int grupoOrdenacao, boolean exigeVagas) {
        this.grupoOrdenacao = grupoOrdenacao;
        this.exigeVagas = exigeVagas;
    }

    public int getGrupoOrdenacao() {
        return grupoOrdenacao;
    }

    /** Conforme o CHECK ck_atu_faixa: TEM_VAGA, POUCAS_VAGAS e LOTADO sempre gravam qt_vagas. */
    public boolean exigeVagas() {
        return exigeVagas;
    }

    public static StatusDisponibilidade de(SituacaoServico situacao) {
        return switch (situacao) {
            case ABERTO -> ABERTO;
            case FILA_GRANDE -> FILA_GRANDE;
            case LOTADO -> LOTADO;
            case FECHADO -> FECHADO;
        };
    }
}
