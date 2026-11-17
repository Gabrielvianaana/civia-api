package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO de T_ATUALIZACAO. */
public interface AtualizacaoDisponibilidadeDAO {

    AtualizacaoDisponibilidade salvar(AtualizacaoDisponibilidade atualizacao);

    Optional<AtualizacaoDisponibilidade> buscarPorId(Long id);

    /** Da mais recente para a mais antiga; {@code servicoId} nulo lista todas. */
    List<AtualizacaoDisponibilidade> listar(Long servicoId);

    /** Mapa servicoId -> atualizacao mais recente. */
    Map<Long, AtualizacaoDisponibilidade> buscarMaisRecentesPorServicos(Collection<Long> servicoIds);

    boolean existePorUsuario(Long usuarioId);

    void excluir(Long id);

    void excluirPorServico(Long servicoId);
}
