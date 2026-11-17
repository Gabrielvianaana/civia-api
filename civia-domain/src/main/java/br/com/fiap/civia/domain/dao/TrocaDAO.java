package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Troca;

import java.util.List;
import java.util.Optional;

/** DAO de T_TROCA. */
public interface TrocaDAO {

    Troca salvar(Troca troca);

    Optional<Troca> buscarPorId(Long id);

    /** {@code cartaoId} nulo lista todas. */
    List<Troca> listar(Long cartaoId);

    boolean existePorCartao(Long cartaoId);

    boolean existePorRecompensa(Long recompensaId);

    void excluir(Long id);
}
