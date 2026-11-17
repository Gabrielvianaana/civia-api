package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.TipoOrganizacao;

import java.util.List;
import java.util.Optional;

/** DAO de T_ORGANIZACAO. O dominio define o contrato; a implementacao (JPA/Oracle) fica na infraestrutura. */
public interface OrganizacaoDAO {

    Organizacao salvar(Organizacao organizacao);

    Optional<Organizacao> buscarPorId(Long id);

    /** {@code tipo} nulo lista todas. */
    List<Organizacao> listar(TipoOrganizacao tipo);

    boolean existePorId(Long id);

    /** Nome unico (un_org_nome); {@code idIgnorado} permite checar na atualizacao. */
    boolean existePorNome(String nome, Long idIgnorado);

    void excluir(Long id);
}
