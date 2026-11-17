package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.ColetaDAO;
import br.com.fiap.civia.domain.model.Coleta;
import br.com.fiap.civia.infrastructure.entity.ColetaEntity;
import br.com.fiap.civia.infrastructure.jpa.ColetaJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** DAO de T_COLETA. */
@Repository
@RequiredArgsConstructor
public class ColetaDAOImpl implements ColetaDAO {

    private final ColetaJpaRepository jpa;

    @Override
    public Coleta salvar(Coleta dominio) {
        ColetaEntity e = dominio.getId() == null ? new ColetaEntity()
                : jpa.findById(dominio.getId()).orElseGet(ColetaEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Coleta> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Coleta> listar(Long postoId) {
        return (postoId == null ? jpa.findAllByOrderByDataColetaDescIdDesc()
                : jpa.findByPostoIdOrderByDataColetaDescIdDesc(postoId)).stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public Optional<LocalDateTime> ultimaColetaDoPosto(Long postoId) {
        return Optional.ofNullable(jpa.ultimaColeta(postoId));
    }

    @Override
    public boolean existePorPosto(Long postoId) {
        return jpa.existsByPostoId(postoId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
