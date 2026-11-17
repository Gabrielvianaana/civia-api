package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Material;

import java.util.List;
import java.util.Optional;

/** DAO de T_MATERIAL. */
public interface MaterialDAO {

    Material salvar(Material material);

    Optional<Material> buscarPorId(Integer id);

    List<Material> listar();

    /** Nome unico (un_material_nome). */
    boolean existePorNome(String nome, Integer idIgnorado);

    long contar();

    void excluir(Integer id);
}
