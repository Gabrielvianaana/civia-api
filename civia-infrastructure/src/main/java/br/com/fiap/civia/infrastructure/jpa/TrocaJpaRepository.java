package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.TrocaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrocaJpaRepository extends JpaRepository<TrocaEntity, Long> {

    List<TrocaEntity> findAllByOrderByDataTrocaDescIdDesc();

    List<TrocaEntity> findByCartaoIdOrderByDataTrocaDescIdDesc(Long cartaoId);

    boolean existsByCartaoId(Long cartaoId);

    boolean existsByRecompensaId(Long recompensaId);
}
