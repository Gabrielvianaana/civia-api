package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.domain.model.TipoOrganizacao;
import br.com.fiap.civia.infrastructure.entity.OrganizacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizacaoJpaRepository extends JpaRepository<OrganizacaoEntity, Long> {

    List<OrganizacaoEntity> findAllByOrderByIdAsc();

    List<OrganizacaoEntity> findByTipoOrderByIdAsc(TipoOrganizacao tipo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
