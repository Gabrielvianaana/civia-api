package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.RecompensaDAO;
import br.com.fiap.civia.domain.model.Recompensa;
import br.com.fiap.civia.infrastructure.entity.RecompensaEntity;
import br.com.fiap.civia.infrastructure.jpa.RecompensaJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_RECOMPENSA. */
@Repository
@RequiredArgsConstructor
public class RecompensaDAOImpl implements RecompensaDAO {

    private final RecompensaJpaRepository jpa;

    @Override
    public Recompensa salvar(Recompensa dominio) {
        RecompensaEntity e = dominio.getId() == null ? new RecompensaEntity()
                : jpa.findById(dominio.getId()).orElseGet(RecompensaEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Recompensa> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Recompensa> listar(Boolean ativo) {
        return (ativo == null ? jpa.findAllByOrderByIdAsc() : jpa.findByAtivoOrderByIdAsc(ativo))
                .stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorNome(String nome, Long idIgnorado) {
        return idIgnorado == null ? jpa.existsByNomeIgnoreCase(nome) : jpa.existsByNomeIgnoreCaseAndIdNot(nome, idIgnorado);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
