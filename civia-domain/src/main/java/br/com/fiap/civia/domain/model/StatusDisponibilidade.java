package br.com.fiap.civia.domain.model;

/**
 * Status exibido no totem para cada servico.
 * O grupo de ordenacao define a posicao na lista: disponiveis primeiro, depois lotados/fechados
 * (US02) e, por ultimo, informacoes sem atualizacao recente (RN05).
 */
public enum StatusDisponibilidade {

    TEM_VAGA(0),
    POUCAS_VAGAS(0),
    ABERTO(0),
    FILA_GRANDE(0),
    LOTADO(1),
    FECHADO(1),
    SEM_ATUALIZACAO_RECENTE(2);

    private final int grupoOrdenacao;

    StatusDisponibilidade(int grupoOrdenacao) {
        this.grupoOrdenacao = grupoOrdenacao;
    }

    public int getGrupoOrdenacao() {
        return grupoOrdenacao;
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
