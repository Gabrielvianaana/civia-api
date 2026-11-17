package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.domain.model.StatusCartao;
import br.com.fiap.civia.infrastructure.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartaoJpaRepository extends JpaRepository<CartaoEntity, Long> {

    List<CartaoEntity> findAllByOrderByIdAsc();

    List<CartaoEntity> findByStatusOrderByIdAsc(StatusCartao status);

    Optional<CartaoEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCartaoAnteriorId(Long cartaoAnteriorId);
}
