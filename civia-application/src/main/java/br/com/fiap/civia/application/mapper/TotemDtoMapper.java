package br.com.fiap.civia.application.mapper;

import br.com.fiap.civia.application.dto.TotemResponse;
import br.com.fiap.civia.domain.model.Totem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TotemDtoMapper {

    public static TotemResponse paraResponse(Totem totem) {
        return new TotemResponse(
                totem.getId(),
                totem.getIdentificacao(),
                totem.getLocal(),
                totem.getCoordenada().latitude(),
                totem.getCoordenada().longitude(),
                totem.isAtivo());
    }
}
