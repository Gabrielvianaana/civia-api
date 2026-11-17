package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.infrastructure.entity.AtualizacaoDisponibilidadeEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AtualizacaoDisponibilidadePersistenceMapper {

    public static AtualizacaoDisponibilidade paraDominio(AtualizacaoDisponibilidadeEntity entity) {
        return AtualizacaoDisponibilidade.reconstituir(
                entity.getId(),
                entity.getServicoId(),
                entity.getVagas(),
                entity.getSituacao(),
                entity.getDataHora());
    }

    public static void copiarParaEntidade(AtualizacaoDisponibilidade atualizacao,
                                          AtualizacaoDisponibilidadeEntity entity) {
        entity.setServicoId(atualizacao.getServicoId());
        entity.setVagas(atualizacao.getVagas());
        entity.setSituacao(atualizacao.getSituacao());
        entity.setDataHora(atualizacao.getDataHora());
    }
}
