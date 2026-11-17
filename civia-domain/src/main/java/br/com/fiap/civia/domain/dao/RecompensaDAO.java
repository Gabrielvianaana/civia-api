package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Recompensa;

import java.util.List;
import java.util.Optional;

/** DAO de T_RECOMPENSA. */
public interface RecompensaDAO {

    Recompensa salvar(Recompensa recompensa);

    Optional<Recompensa> buscarPorId(Long id);

    /** {@code ativo} nulo lista todas. */
    List<Recompensa> listar(Boolean ativo);

    /** Nome unico (un_recompensa_nome). */
    boolean existePorNome(String nome, Long idIgnorado);

    void excluir(Long id);
}
