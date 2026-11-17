package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import br.com.fiap.civia.infrastructure.jpa.TotemJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.TotemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Implementacao do TotemDAO com Spring Data JPA sobre o Oracle. */
@Repository
@RequiredArgsConstructor
public class TotemDAOImpl implements TotemDAO {

    private final TotemJpaRepository jpaRepository;

    @Override
    public Totem salvar(Totem totem) {
        TotemEntity entity = totem.getId() == null
                ? new TotemEntity()
                : jpaRepository.findById(totem.getId()).orElseGet(TotemEntity::new);
        TotemPersistenceMapper.copiarParaEntidade(totem, entity);
        return TotemPersistenceMapper.paraDominio(jpaRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<Totem> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(TotemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Totem> listar() {
        return jpaRepository.findAllByOrderByIdAsc().stream().map(TotemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorIdentificacao(String identificacao) {
        return jpaRepository.existsByIdentificacaoIgnoreCase(identificacao);
    }

    @Override
    public boolean existePorIdentificacaoEmOutroTotem(String identificacao, Long idTotem) {
        return jpaRepository.existsByIdentificacaoIgnoreCaseAndIdNot(identificacao, idTotem);
    }

    @Override
    public long contar() {
        return jpaRepository.count();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }
}
