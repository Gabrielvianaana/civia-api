package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.UsuarioPainelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioPainelJpaRepository extends JpaRepository<UsuarioPainelEntity, Long> {

    List<UsuarioPainelEntity> findAllByOrderByIdAsc();

    List<UsuarioPainelEntity> findByOrganizacaoIdOrderByIdAsc(Long organizacaoId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByOrganizacaoId(Long organizacaoId);
}
