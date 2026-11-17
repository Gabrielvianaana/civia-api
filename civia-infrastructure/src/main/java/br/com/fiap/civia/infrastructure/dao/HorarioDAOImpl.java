package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.HorarioDAO;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.infrastructure.entity.HorarioEntity;
import br.com.fiap.civia.infrastructure.jpa.HorarioJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.OperacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** DAO de T_HORARIO. */
@Repository
@RequiredArgsConstructor
public class HorarioDAOImpl implements HorarioDAO {

    private final HorarioJpaRepository jpa;

    @Override
    public Horario salvar(Horario horario) {
        HorarioEntity e = horario.getId() == null ? new HorarioEntity()
                : jpa.findById(horario.getId()).orElseGet(HorarioEntity::new);
        OperacaoPersistenceMapper.copiar(horario, e);
        return OperacaoPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Horario> buscarPorId(Long id) {
        return jpa.findById(id).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Horario> listarPorServico(Long servicoId) {
        return jpa.findByServicoIdOrderByDiaSemanaAscAberturaAsc(servicoId).stream()
                .map(OperacaoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public Map<Long, List<Horario>> listarPorServicos(Collection<Long> servicoIds) {
        if (servicoIds == null || servicoIds.isEmpty()) {
            return Map.of();
        }
        return jpa.findByServicoIdIn(servicoIds).stream()
                .map(OperacaoPersistenceMapper::paraDominio)
                .collect(Collectors.groupingBy(Horario::getServicoId));
    }

    @Override
    public boolean existe(Long servicoId, int diaSemana, LocalTime abertura, Long idIgnorado) {
        return idIgnorado == null
                ? jpa.existsByServicoIdAndDiaSemanaAndAbertura(servicoId, diaSemana, abertura)
                : jpa.existsByServicoIdAndDiaSemanaAndAberturaAndIdNot(servicoId, diaSemana, abertura, idIgnorado);
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
