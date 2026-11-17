package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.RecompensaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecompensaJpaRepository extends JpaRepository<RecompensaEntity, Long> {

    List<RecompensaEntity> findAllByOrderByIdAsc();

    List<RecompensaEntity> findByAtivoOrderByIdAsc(Boolean ativo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
