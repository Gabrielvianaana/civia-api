package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TotemJpaRepository extends JpaRepository<TotemEntity, Long> {

    List<TotemEntity> findAllByOrderByIdAsc();

    boolean existsByIdentificacaoIgnoreCase(String identificacao);

    boolean existsByIdentificacaoIgnoreCaseAndIdNot(String identificacao, Long id);
}
