package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.infrastructure.entity.AtualizacaoDisponibilidadeEntity;
import br.com.fiap.civia.infrastructure.jpa.AtualizacaoDisponibilidadeJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.OperacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/** DAO de T_ATUALIZACAO. */
@Repository
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeDAOImpl implements AtualizacaoDisponibilidadeDAO {

    private final AtualizacaoDisponibilidadeJpaRepository jpa;

    @Override
    public AtualizacaoDisponibilidade salvar(AtualizacaoDisponibilidade atualizacao) {
        AtualizacaoDisponibilidadeEntity e = atualizacao.getId() == null ? new AtualizacaoDisponibilidadeEntity()
                : jpa.findById(atualizacao.getId()).orElseGet(AtualizacaoDisponibilidadeEntity::new);
        OperacaoPersistenceMapper.copiar(atualizacao, e);
        return OperacaoPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<AtualizacaoDisponibilidade> buscarPorId(Long id) {
        return jpa.findById(id).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public List<AtualizacaoDisponibilidade> listar(Long servicoId) {
        return (servicoId == null ? jpa.findAllByOrderByDataHoraDescIdDesc()
                : jpa.findByServicoIdOrderByDataHoraDescIdDesc(servicoId))
                .stream().map(OperacaoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public Map<Long, AtualizacaoDisponibilidade> buscarMaisRecentesPorServicos(Collection<Long> servicoIds) {
        if (servicoIds == null || servicoIds.isEmpty()) {
            return Map.of();
        }
        // Empate na data/hora: prevalece o registro de maior id (o ultimo gravado).
        BinaryOperator<AtualizacaoDisponibilidade> maisRecente = BinaryOperator.maxBy(
                Comparator.comparing(AtualizacaoDisponibilidade::getDataHora)
                        .thenComparing(AtualizacaoDisponibilidade::getId));
        return jpa.findMaisRecentesPorServicos(servicoIds).stream()
                .map(OperacaoPersistenceMapper::paraDominio)
                .collect(Collectors.toMap(AtualizacaoDisponibilidade::getServicoId, a -> a, maisRecente));
    }

    @Override
    public boolean existePorUsuario(Long usuarioId) {
        return jpa.existsByUsuarioId(usuarioId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }

    @Override
    public void excluirPorServico(Long servicoId) {
        jpa.excluirPorServico(servicoId);
    }
}
