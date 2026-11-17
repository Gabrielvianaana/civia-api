package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.infrastructure.entity.PerfilAtendidoEntity;
import br.com.fiap.civia.infrastructure.jpa.PerfilAtendidoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReferenciaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** DAO de T_PERFIL_ATENDIDO. */
@Repository
@RequiredArgsConstructor
public class PerfilAtendidoDAOImpl implements PerfilAtendidoDAO {

    private final PerfilAtendidoJpaRepository jpa;

    @Override
    public PerfilAtendido salvar(PerfilAtendido perfil) {
        PerfilAtendidoEntity e = perfil.getId() == null ? new PerfilAtendidoEntity()
                : jpa.findById(perfil.getId()).orElseGet(PerfilAtendidoEntity::new);
        ReferenciaPersistenceMapper.copiar(perfil, e);
        return ReferenciaPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<PerfilAtendido> buscarPorId(Integer id) {
        return jpa.findById(id).map(ReferenciaPersistenceMapper::paraDominio);
    }

    @Override
    public List<PerfilAtendido> buscarPorIds(Collection<Integer> ids) {
        return jpa.findAllById(ids).stream().map(ReferenciaPersistenceMapper::paraDominio).toList();
    }

    @Override
    public List<PerfilAtendido> listar() {
        return jpa.findAllByOrderByIdAsc().stream().map(ReferenciaPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorDescricao(String descricao, Integer idIgnorado) {
        return idIgnorado == null ? jpa.existsByDescricaoIgnoreCase(descricao)
                : jpa.existsByDescricaoIgnoreCaseAndIdNot(descricao, idIgnorado);
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
