package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.PerfilAtendido;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** DAO de T_PERFIL_ATENDIDO. */
public interface PerfilAtendidoDAO {

    PerfilAtendido salvar(PerfilAtendido perfil);

    Optional<PerfilAtendido> buscarPorId(Integer id);

    List<PerfilAtendido> buscarPorIds(Collection<Integer> ids);

    List<PerfilAtendido> listar();

    boolean existePorDescricao(String descricao, Integer idIgnorado);

    long contar();

    void excluir(Integer id);
}
