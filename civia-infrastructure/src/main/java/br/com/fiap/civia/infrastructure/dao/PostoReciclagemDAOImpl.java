package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.PostoReciclagemDAO;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.StatusPosto;
import br.com.fiap.civia.infrastructure.entity.PostoReciclagemEntity;
import br.com.fiap.civia.infrastructure.jpa.PostoReciclagemJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_POSTO_RECICLAGEM. */
@Repository
@RequiredArgsConstructor
public class PostoReciclagemDAOImpl implements PostoReciclagemDAO {

    private final PostoReciclagemJpaRepository jpa;

    @Override
    public PostoReciclagem salvar(PostoReciclagem dominio) {
        PostoReciclagemEntity e = dominio.getId() == null ? new PostoReciclagemEntity()
                : jpa.findById(dominio.getId()).orElseGet(PostoReciclagemEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<PostoReciclagem> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<PostoReciclagem> listar(StatusPosto status) {
        return (status == null ? jpa.findAllByOrderByIdAsc() : jpa.findByStatusOrderByIdAsc(status))
                .stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorTotem(Long totemId, Long idIgnorado) {
        return idIgnorado == null ? jpa.existsByTotemId(totemId) : jpa.existsByTotemIdAndIdNot(totemId, idIgnorado);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
