package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, Long> {

    /** Filtros opcionais: parametro nulo nao filtra. */
    @Query("""
            select s from ServicoEntity s
             where (:tipoId is null or s.tipo.id = :tipoId)
               and (:status is null or s.statusAprovacao = :status)
               and (:organizacaoId is null or s.organizacaoId = :organizacaoId)
             order by s.id
            """)
    List<ServicoEntity> filtrar(@Param("tipoId") Integer tipoId,
                                @Param("status") StatusAprovacao status,
                                @Param("organizacaoId") Long organizacaoId);

    boolean existsByOrganizacaoId(Long organizacaoId);

    boolean existsByTipoId(Integer tipoId);

    boolean existsByPerfisId(Integer perfilId);
}
