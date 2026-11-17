package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TotemPersistenceMapper {

    public static Totem paraDominio(TotemEntity entity) {
        return Totem.reconstituir(
                entity.getId(),
                entity.getIdentificacao(),
                entity.getLocal(),
                new Coordenada(entity.getLatitude().doubleValue(), entity.getLongitude().doubleValue()),
                Boolean.TRUE.equals(entity.getAtivo()));
    }

    public static void copiarParaEntidade(Totem totem, TotemEntity entity) {
        entity.setIdentificacao(totem.getIdentificacao());
        entity.setLocal(totem.getLocal());
        entity.setLatitude(CoordenadaMapper.paraDecimal(totem.getCoordenada().latitude()));
        entity.setLongitude(CoordenadaMapper.paraDecimal(totem.getCoordenada().longitude()));
        entity.setAtivo(totem.isAtivo());
    }
}
