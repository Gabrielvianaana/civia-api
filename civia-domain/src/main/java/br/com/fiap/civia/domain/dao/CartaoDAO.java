package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.StatusCartao;

import java.util.List;
import java.util.Optional;

/** DAO de T_CARTAO. */
public interface CartaoDAO {

    Cartao salvar(Cartao cartao);

    Optional<Cartao> buscarPorId(Long id);

    Optional<Cartao> buscarPorCodigo(String codigo);

    /** {@code status} nulo lista todos. */
    List<Cartao> listar(StatusCartao status);

    /** Codigo unico (un_cartao_codigo). */
    boolean existePorCodigo(String codigo);

    /** O cartao ja foi substituido por outro (un_cartao_anterior). */
    boolean existeSubstituto(Long cartaoAnteriorId);

    void excluir(Long id);
}
