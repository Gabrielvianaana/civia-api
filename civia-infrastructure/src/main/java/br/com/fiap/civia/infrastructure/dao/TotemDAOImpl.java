package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import br.com.fiap.civia.infrastructure.jpa.TotemJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.OperacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_TOTEM. */
@Repository
@RequiredArgsConstructor
public class TotemDAOImpl implements TotemDAO {

    private final TotemJpaRepository jpa;

    @Override
    public Totem salvar(Totem totem) {
        TotemEntity e = totem.getId() == null ? new TotemEntity()
                : jpa.findById(totem.getId()).orElseGet(TotemEntity::new);
        OperacaoPersistenceMapper.copiar(totem, e);
        return OperacaoPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Totem> buscarPorId(Long id) {
        return jpa.findById(id).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Totem> listar() {
        return jpa.findAllByOrderByIdAsc().stream().map(OperacaoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorCodigo(String codigo, Long idIgnorado) {
        return idIgnorado == null ? jpa.existsByCodigoIgnoreCase(codigo) : jpa.existsByCodigoIgnoreCaseAndIdNot(codigo, idIgnorado);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
