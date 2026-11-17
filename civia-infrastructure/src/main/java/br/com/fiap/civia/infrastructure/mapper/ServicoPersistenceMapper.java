package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.valueobject.Endereco;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServicoPersistenceMapper {

    public static Servico paraDominio(ServicoEntity e) {
        return Servico.reconstituir(
                e.getId(),
                e.getOrganizacaoId(),
                ReferenciaPersistenceMapper.paraDominio(e.getTipo()),
                e.getNome(),
                new Endereco(e.getLogradouro(), e.getNumero(), e.getBairro()),
                CoordenadaMapper.paraDominio(e.getLatitude(), e.getLongitude()),
                e.getCapacidade(),
                Boolean.TRUE.equals(e.getFunciona24h()),
                e.getStatusAprovacao(),
                e.getDataCadastro(),
                e.getPerfis().stream()
                        .map(ReferenciaPersistenceMapper::paraDominio)
                        .sorted(Comparator.comparing(PerfilAtendido::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    /** Copia os campos simples; tipo e perfis sao associados pelo DAO (referencias JPA). */
    public static void copiar(Servico s, ServicoEntity e) {
        e.setOrganizacaoId(s.getOrganizacaoId());
        e.setNome(s.getNome());
        e.setLogradouro(s.getEndereco().logradouro());
        e.setNumero(s.getEndereco().numero());
        e.setBairro(s.getEndereco().bairro());
        e.setLatitude(CoordenadaMapper.paraDecimal(s.getCoordenada().latitude()));
        e.setLongitude(CoordenadaMapper.paraDecimal(s.getCoordenada().longitude()));
        e.setCapacidade(s.getCapacidade());
        e.setFunciona24h(s.isFunciona24h());
        e.setStatusAprovacao(s.getStatusAprovacao());
        e.setDataCadastro(s.getDataCadastro());
    }
}
