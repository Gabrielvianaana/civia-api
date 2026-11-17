package br.com.fiap.civia.application.mapper;

import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AtualizacaoDisponibilidadeDtoMapper {

    public static AtualizacaoDisponibilidadeResponse paraResponse(AtualizacaoDisponibilidade atualizacao) {
        return new AtualizacaoDisponibilidadeResponse(
                atualizacao.getId(),
                atualizacao.getServicoId(),
                atualizacao.getVagas(),
                atualizacao.getSituacao() == null ? null : atualizacao.getSituacao().name(),
                atualizacao.getDataHora());
    }
}
