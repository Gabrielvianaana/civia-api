package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.TipoServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoServicoJpaRepository extends JpaRepository<TipoServicoEntity, Integer> {

    List<TipoServicoEntity> findAllByOrderByIdAsc();

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);
}
