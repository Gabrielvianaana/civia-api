package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.infrastructure.entity.TipoServicoEntity;
import br.com.fiap.civia.infrastructure.jpa.TipoServicoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReferenciaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_TIPO_SERVICO. */
@Repository
@RequiredArgsConstructor
public class TipoServicoDAOImpl implements TipoServicoDAO {

    private final TipoServicoJpaRepository jpa;

    @Override
    public TipoServico salvar(TipoServico tipo) {
        TipoServicoEntity e = tipo.getId() == null ? new TipoServicoEntity()
                : jpa.findById(tipo.getId()).orElseGet(TipoServicoEntity::new);
        ReferenciaPersistenceMapper.copiar(tipo, e);
        return ReferenciaPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<TipoServico> buscarPorId(Integer id) {
        return jpa.findById(id).map(ReferenciaPersistenceMapper::paraDominio);
    }

    @Override
    public List<TipoServico> listar() {
        return jpa.findAllByOrderByIdAsc().stream().map(ReferenciaPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorNome(String nome, Integer idIgnorado) {
        return idIgnorado == null ? jpa.existsByNomeIgnoreCase(nome) : jpa.existsByNomeIgnoreCaseAndIdNot(nome, idIgnorado);
    }

    @Override
    public long contar() {
        return jpa.count();
    }

    @Override
    public void excluir(Integer id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
