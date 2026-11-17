package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.TipoServico;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) de servicos. O dominio define o contrato; a implementacao
 * (JPA + Oracle) fica na camada de infraestrutura.
 */
public interface ServicoDAO {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    /** Lista com filtros opcionais: {@code null} significa "sem filtro". */
    List<Servico> listar(TipoServico tipo, Boolean aprovado);

    boolean existePorId(Long id);

    long contar();

    void excluir(Long id);
}
