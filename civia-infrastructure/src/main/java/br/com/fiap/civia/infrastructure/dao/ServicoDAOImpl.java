package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import br.com.fiap.civia.infrastructure.jpa.ServicoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ServicoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Implementacao do ServicoDAO com Spring Data JPA sobre o Oracle. */
@Repository
@RequiredArgsConstructor
public class ServicoDAOImpl implements ServicoDAO {

    private final ServicoJpaRepository jpaRepository;

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity entity = servico.getId() == null
                ? new ServicoEntity()
                : jpaRepository.findById(servico.getId()).orElseGet(ServicoEntity::new);
        ServicoPersistenceMapper.copiarParaEntidade(servico, entity);
        return ServicoPersistenceMapper.paraDominio(jpaRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ServicoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Servico> listar(TipoServico tipo, Boolean aprovado) {
        List<ServicoEntity> entidades;
        if (tipo != null && aprovado != null) {
            entidades = jpaRepository.findByTipoAndAprovadoOrderByIdAsc(tipo, aprovado);
        } else if (tipo != null) {
            entidades = jpaRepository.findByTipoOrderByIdAsc(tipo);
        } else if (aprovado != null) {
            entidades = jpaRepository.findByAprovadoOrderByIdAsc(aprovado);
        } else {
            entidades = jpaRepository.findAllByOrderByIdAsc();
        }
        return entidades.stream().map(ServicoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
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
