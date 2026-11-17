package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;

/** Traduz entre o modelo relacional (JPA) e o modelo de dominio puro. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServicoPersistenceMapper {

    public static Servico paraDominio(ServicoEntity entity) {
        return Servico.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getTipo(),
                entity.getEndereco(),
                new Coordenada(entity.getLatitude().doubleValue(), entity.getLongitude().doubleValue()),
                new HorarioFuncionamento(entity.getHoraAbertura(), entity.getHoraFechamento()),
                entity.getPerfis(),
                Boolean.TRUE.equals(entity.getAprovado()),
                entity.getDataCadastro());
    }

    public static void copiarParaEntidade(Servico servico, ServicoEntity entity) {
        entity.setNome(servico.getNome());
        entity.setTipo(servico.getTipo());
        entity.setEndereco(servico.getEndereco());
        entity.setLatitude(CoordenadaMapper.paraDecimal(servico.getCoordenada().latitude()));
        entity.setLongitude(CoordenadaMapper.paraDecimal(servico.getCoordenada().longitude()));
        entity.setHoraAbertura(servico.getHorario().abertura());
        entity.setHoraFechamento(servico.getHorario().fechamento());
        entity.setAprovado(servico.isAprovado());
        entity.setDataCadastro(servico.getDataCadastro());
        if (entity.getPerfis() == null) {
            entity.setPerfis(new HashSet<>());
        }
        entity.getPerfis().clear();
        entity.getPerfis().addAll(servico.getPerfis());
    }
}
