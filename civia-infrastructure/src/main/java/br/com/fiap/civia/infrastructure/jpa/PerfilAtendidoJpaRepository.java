package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.PerfilAtendidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfilAtendidoJpaRepository extends JpaRepository<PerfilAtendidoEntity, Integer> {

    List<PerfilAtendidoEntity> findAllByOrderByIdAsc();

    boolean existsByDescricaoIgnoreCase(String descricao);

    boolean existsByDescricaoIgnoreCaseAndIdNot(String descricao, Integer id);
}
