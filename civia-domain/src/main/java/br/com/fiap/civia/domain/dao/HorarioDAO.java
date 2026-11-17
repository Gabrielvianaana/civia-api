package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Horario;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO de T_HORARIO. */
public interface HorarioDAO {

    Horario salvar(Horario horario);

    Optional<Horario> buscarPorId(Long id);

    List<Horario> listarPorServico(Long servicoId);

    /** Mapa servicoId -> horarios, para montar a grade de varios servicos em uma consulta. */
    Map<Long, List<Horario>> listarPorServicos(Collection<Long> servicoIds);

    /** Unicidade un_horario (servico + dia + abertura). */
    boolean existe(Long servicoId, int diaSemana, LocalTime abertura, Long idIgnorado);

    void excluir(Long id);

    void excluirPorServico(Long servicoId);
}
