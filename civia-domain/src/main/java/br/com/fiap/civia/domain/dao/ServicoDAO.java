package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusAprovacao;

import java.util.List;
import java.util.Optional;

/** DAO de T_SERVICO (com a associativa T_SERVICO_PERFIL). */
public interface ServicoDAO {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    /** Filtros opcionais: {@code null} significa "sem filtro". */
    List<Servico> listar(Integer tipoServicoId, StatusAprovacao statusAprovacao, Long organizacaoId);

    boolean existePorId(Long id);

    boolean existePorOrganizacao(Long organizacaoId);

    boolean existePorTipo(Integer tipoServicoId);

    boolean existePorPerfil(Integer perfilId);

    void excluir(Long id);
}
