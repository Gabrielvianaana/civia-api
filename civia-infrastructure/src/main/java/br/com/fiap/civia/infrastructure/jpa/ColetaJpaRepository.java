package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.ColetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ColetaJpaRepository extends JpaRepository<ColetaEntity, Long> {

    List<ColetaEntity> findAllByOrderByDataColetaDescIdDesc();

    List<ColetaEntity> findByPostoIdOrderByDataColetaDescIdDesc(Long postoId);

    @Query("select max(c.dataColeta) from ColetaEntity c where c.postoId = :postoId")
    LocalDateTime ultimaColeta(@Param("postoId") Long postoId);

    boolean existsByPostoId(Long postoId);
}
