package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TotemJpaRepository extends JpaRepository<TotemEntity, Long> {

    List<TotemEntity> findAllByOrderByIdAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
