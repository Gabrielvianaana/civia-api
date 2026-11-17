package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.domain.model.StatusPosto;
import br.com.fiap.civia.infrastructure.entity.PostoReciclagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostoReciclagemJpaRepository extends JpaRepository<PostoReciclagemEntity, Long> {

    List<PostoReciclagemEntity> findAllByOrderByIdAsc();

    List<PostoReciclagemEntity> findByStatusOrderByIdAsc(StatusPosto status);

    boolean existsByTotemId(Long totemId);

    boolean existsByTotemIdAndIdNot(Long totemId, Long id);
}
