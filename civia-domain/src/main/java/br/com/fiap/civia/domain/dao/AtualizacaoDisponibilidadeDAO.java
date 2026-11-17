package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO das atualizacoes de disponibilidade. */
public interface AtualizacaoDisponibilidadeDAO {

    AtualizacaoDisponibilidade salvar(AtualizacaoDisponibilidade atualizacao);

    Optional<AtualizacaoDisponibilidade> buscarPorId(Long id);

    /** Lista da mais recente para a mais antiga; {@code servicoId} nulo lista todas. */
    List<AtualizacaoDisponibilidade> listar(Long servicoId);

    Optional<AtualizacaoDisponibilidade> buscarMaisRecentePorServico(Long servicoId);

    /** Mapa servicoId -> atualizacao mais recente, para os servicos informados. */
    Map<Long, AtualizacaoDisponibilidade> buscarMaisRecentesPorServicos(Collection<Long> servicoIds);

    void excluir(Long id);

    void excluirPorServico(Long servicoId);
}
