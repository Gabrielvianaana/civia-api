package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.UsuarioPainel;

import java.util.List;
import java.util.Optional;

/** DAO de T_USUARIO_PAINEL. */
public interface UsuarioPainelDAO {

    UsuarioPainel salvar(UsuarioPainel usuario);

    Optional<UsuarioPainel> buscarPorId(Long id);

    /** {@code organizacaoId} nulo lista todos. */
    List<UsuarioPainel> listar(Long organizacaoId);

    boolean existePorEmail(String email, Long idIgnorado);

    boolean existePorOrganizacao(Long organizacaoId);

    void excluir(Long id);
}
