package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.EntregaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EntregaJpaRepository extends JpaRepository<EntregaEntity, Long> {

    /** Filtros opcionais: parametro nulo nao filtra. */
    @Query("""
            select e from EntregaEntity e
             where (:cartaoId is null or e.cartaoId = :cartaoId)
               and (:postoId is null or e.postoId = :postoId)
             order by e.dataEntrega desc, e.id desc
            """)
    List<EntregaEntity> filtrar(@Param("cartaoId") Long cartaoId, @Param("postoId") Long postoId);

    @Query("""
            select coalesce(sum(e.pesoKg), 0) from EntregaEntity e
             where e.cartaoId = :cartaoId and e.dataEntrega >= :inicio and e.dataEntrega < :fim and e.id <> :idIgnorado
            """)
    BigDecimal somarPesoDoCartao(@Param("cartaoId") Long cartaoId, @Param("inicio") LocalDateTime inicio,
                                 @Param("fim") LocalDateTime fim, @Param("idIgnorado") Long idIgnorado);

    @Query("""
            select coalesce(sum(e.pesoKg), 0) from EntregaEntity e
             where e.postoId = :postoId and e.dataEntrega > :desde and e.id <> :idIgnorado
            """)
    BigDecimal somarPesoDoPostoDesde(@Param("postoId") Long postoId, @Param("desde") LocalDateTime desde,
                                     @Param("idIgnorado") Long idIgnorado);

    @Query("select coalesce(sum(e.pesoKg), 0) from EntregaEntity e where e.postoId = :postoId and e.id <> :idIgnorado")
    BigDecimal somarPesoDoPosto(@Param("postoId") Long postoId, @Param("idIgnorado") Long idIgnorado);

    boolean existsByCartaoId(Long cartaoId);

    boolean existsByPostoId(Long postoId);

    boolean existsByMaterialId(Integer materialId);
}
