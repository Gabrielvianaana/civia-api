package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.StatusPosto;

import java.util.List;
import java.util.Optional;

/** DAO de T_POSTO_RECICLAGEM. */
public interface PostoReciclagemDAO {

    PostoReciclagem salvar(PostoReciclagem posto);

    Optional<PostoReciclagem> buscarPorId(Long id);

    /** {@code status} nulo lista todos. */
    List<PostoReciclagem> listar(StatusPosto status);

    /** Um posto por totem (un_posto_totem). */
    boolean existePorTotem(Long totemId, Long idIgnorado);

    void excluir(Long id);
}
