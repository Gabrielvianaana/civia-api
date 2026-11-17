package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.AtualizacaoDisponibilidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AtualizacaoDisponibilidadeJpaRepository extends JpaRepository<AtualizacaoDisponibilidadeEntity, Long> {

    List<AtualizacaoDisponibilidadeEntity> findAllByOrderByDataHoraDescIdDesc();

    List<AtualizacaoDisponibilidadeEntity> findByServicoIdOrderByDataHoraDescIdDesc(Long servicoId);

    /** Somente a(s) atualizacao(oes) mais recente(s) de cada servico informado. */
    @Query("""
            select a from AtualizacaoDisponibilidadeEntity a
             where a.servicoId in :servicoIds
               and a.dataHora = (select max(b.dataHora) from AtualizacaoDisponibilidadeEntity b
                                  where b.servicoId = a.servicoId)
            """)
    List<AtualizacaoDisponibilidadeEntity> findMaisRecentesPorServicos(@Param("servicoIds") Collection<Long> servicoIds);

    boolean existsByUsuarioId(Long usuarioId);

    @Modifying
    @Query("delete from AtualizacaoDisponibilidadeEntity a where a.servicoId = :servicoId")
    void excluirPorServico(@Param("servicoId") Long servicoId);
}
