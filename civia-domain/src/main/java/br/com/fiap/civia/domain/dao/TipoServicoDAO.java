package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.TipoServico;

import java.util.List;
import java.util.Optional;

/** DAO de T_TIPO_SERVICO. */
public interface TipoServicoDAO {

    TipoServico salvar(TipoServico tipo);

    Optional<TipoServico> buscarPorId(Integer id);

    List<TipoServico> listar();

    boolean existePorNome(String nome, Integer idIgnorado);

    long contar();

    void excluir(Integer id);
}
