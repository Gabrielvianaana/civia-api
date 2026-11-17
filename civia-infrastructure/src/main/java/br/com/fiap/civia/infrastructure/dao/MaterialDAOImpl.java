package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.MaterialDAO;
import br.com.fiap.civia.domain.model.Material;
import br.com.fiap.civia.infrastructure.entity.MaterialEntity;
import br.com.fiap.civia.infrastructure.jpa.MaterialJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_MATERIAL. */
@Repository
@RequiredArgsConstructor
public class MaterialDAOImpl implements MaterialDAO {

    private final MaterialJpaRepository jpa;

    @Override
    public Material salvar(Material dominio) {
        MaterialEntity e = dominio.getId() == null ? new MaterialEntity()
                : jpa.findById(dominio.getId()).orElseGet(MaterialEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Material> buscarPorId(Integer id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Material> listar() {
        return jpa.findAllByOrderByIdAsc().stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
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
