package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.TrocaDAO;
import br.com.fiap.civia.domain.model.Troca;
import br.com.fiap.civia.infrastructure.entity.TrocaEntity;
import br.com.fiap.civia.infrastructure.jpa.TrocaJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_TROCA. */
@Repository
@RequiredArgsConstructor
public class TrocaDAOImpl implements TrocaDAO {

    private final TrocaJpaRepository jpa;

    @Override
    public Troca salvar(Troca dominio) {
        TrocaEntity e = dominio.getId() == null ? new TrocaEntity()
                : jpa.findById(dominio.getId()).orElseGet(TrocaEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Troca> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Troca> listar(Long cartaoId) {
        return (cartaoId == null ? jpa.findAllByOrderByDataTrocaDescIdDesc()
                : jpa.findByCartaoIdOrderByDataTrocaDescIdDesc(cartaoId)).stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorCartao(Long cartaoId) {
        return jpa.existsByCartaoId(cartaoId);
    }

    @Override
    public boolean existePorRecompensa(Long recompensaId) {
        return jpa.existsByRecompensaId(recompensaId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
