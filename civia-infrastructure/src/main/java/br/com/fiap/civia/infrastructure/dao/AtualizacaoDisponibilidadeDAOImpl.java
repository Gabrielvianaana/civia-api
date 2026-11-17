package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.infrastructure.entity.AtualizacaoDisponibilidadeEntity;
import br.com.fiap.civia.infrastructure.jpa.AtualizacaoDisponibilidadeJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.AtualizacaoDisponibilidadePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/** Implementacao do AtualizacaoDisponibilidadeDAO com Spring Data JPA sobre o Oracle. */
@Repository
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeDAOImpl implements AtualizacaoDisponibilidadeDAO {

    private final AtualizacaoDisponibilidadeJpaRepository jpaRepository;

    @Override
    public AtualizacaoDisponibilidade salvar(AtualizacaoDisponibilidade atualizacao) {
        AtualizacaoDisponibilidadeEntity entity = atualizacao.getId() == null
                ? new AtualizacaoDisponibilidadeEntity()
                : jpaRepository.findById(atualizacao.getId()).orElseGet(AtualizacaoDisponibilidadeEntity::new);
        AtualizacaoDisponibilidadePersistenceMapper.copiarParaEntidade(atualizacao, entity);
        return AtualizacaoDisponibilidadePersistenceMapper.paraDominio(jpaRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<AtualizacaoDisponibilidade> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(AtualizacaoDisponibilidadePersistenceMapper::paraDominio);
    }

    @Override
    public List<AtualizacaoDisponibilidade> listar(Long servicoId) {
        List<AtualizacaoDisponibilidadeEntity> entidades = servicoId == null
                ? jpaRepository.findAllByOrderByDataHoraDescIdDesc()
                : jpaRepository.findByServicoIdOrderByDataHoraDescIdDesc(servicoId);
        return entidades.stream().map(AtualizacaoDisponibilidadePersistenceMapper::paraDominio).toList();
    }

    @Override
    public Optional<AtualizacaoDisponibilidade> buscarMaisRecentePorServico(Long servicoId) {
        return jpaRepository.findFirstByServicoIdOrderByDataHoraDescIdDesc(servicoId)
                .map(AtualizacaoDisponibilidadePersistenceMapper::paraDominio);
    }

    @Override
    public Map<Long, AtualizacaoDisponibilidade> buscarMaisRecentesPorServicos(Collection<Long> servicoIds) {
        if (servicoIds == null || servicoIds.isEmpty()) {
            return Map.of();
        }
        // Em caso de empate na data/hora, prevalece o registro de maior id (o ultimo gravado).
        BinaryOperator<AtualizacaoDisponibilidade> maisRecente = BinaryOperator.maxBy(
                Comparator.comparing(AtualizacaoDisponibilidade::getDataHora)
                        .thenComparing(AtualizacaoDisponibilidade::getId));
        return jpaRepository.findMaisRecentesPorServicos(servicoIds).stream()
                .map(AtualizacaoDisponibilidadePersistenceMapper::paraDominio)
                .collect(Collectors.toMap(AtualizacaoDisponibilidade::getServicoId, a -> a, maisRecente));
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void excluirPorServico(Long servicoId) {
        jpaRepository.excluirPorServico(servicoId);
    }
}
