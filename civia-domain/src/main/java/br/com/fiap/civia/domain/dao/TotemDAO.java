package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Totem;

import java.util.List;
import java.util.Optional;

/** DAO de T_TOTEM. */
public interface TotemDAO {

    Totem salvar(Totem totem);

    Optional<Totem> buscarPorId(Long id);

    List<Totem> listar();

    /** Codigo unico (un_totem_codigo). */
    boolean existePorCodigo(String codigo, Long idIgnorado);

    void excluir(Long id);
}
