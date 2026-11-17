package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import br.com.fiap.civia.infrastructure.entity.OrganizacaoEntity;
import br.com.fiap.civia.infrastructure.jpa.OrganizacaoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReferenciaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_ORGANIZACAO com Spring Data JPA sobre o Oracle. */
@Repository
@RequiredArgsConstructor
public class OrganizacaoDAOImpl implements OrganizacaoDAO {

    private final OrganizacaoJpaRepository jpa;

    @Override
    public Organizacao salvar(Organizacao organizacao) {
        OrganizacaoEntity e = organizacao.getId() == null ? new OrganizacaoEntity()
                : jpa.findById(organizacao.getId()).orElseGet(OrganizacaoEntity::new);
        ReferenciaPersistenceMapper.copiar(organizacao, e);
        return ReferenciaPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Organizacao> buscarPorId(Long id) {
        return jpa.findById(id).map(ReferenciaPersistenceMapper::paraDominio);
    }

    @Override
    public List<Organizacao> listar(TipoOrganizacao tipo) {
        return (tipo == null ? jpa.findAllByOrderByIdAsc() : jpa.findByTipoOrderByIdAsc(tipo)).stream()
                .map(ReferenciaPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existePorNome(String nome, Long idIgnorado) {
        return idIgnorado == null ? jpa.existsByNomeIgnoreCase(nome) : jpa.existsByNomeIgnoreCaseAndIdNot(nome, idIgnorado);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
