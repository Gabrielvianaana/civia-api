package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface HorarioJpaRepository extends JpaRepository<HorarioEntity, Long> {

    List<HorarioEntity> findByServicoIdOrderByDiaSemanaAscAberturaAsc(Long servicoId);

    List<HorarioEntity> findByServicoIdIn(Collection<Long> servicoIds);

    boolean existsByServicoIdAndDiaSemanaAndAbertura(Long servicoId, Integer diaSemana, LocalTime abertura);

    boolean existsByServicoIdAndDiaSemanaAndAberturaAndIdNot(Long servicoId, Integer diaSemana, LocalTime abertura, Long id);

    @Modifying
    @Query("delete from HorarioEntity h where h.servicoId = :servicoId")
    void excluirPorServico(@Param("servicoId") Long servicoId);
}
