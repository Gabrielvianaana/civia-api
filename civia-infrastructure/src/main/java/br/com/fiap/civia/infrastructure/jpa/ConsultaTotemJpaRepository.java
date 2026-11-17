package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.infrastructure.entity.ConsultaTotemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultaTotemJpaRepository extends JpaRepository<ConsultaTotemEntity, Long> {

    List<ConsultaTotemEntity> findAllByOrderByDataConsultaDescIdDesc();

    List<ConsultaTotemEntity> findByTotemIdOrderByDataConsultaDescIdDesc(Long totemId);

    Optional<ConsultaTotemEntity> findByCodigoChegada(String codigoChegada);

    boolean existsByCodigoChegada(String codigoChegada);

    boolean existsByTotemId(Long totemId);

    boolean existsByServicoEscolhidoId(Long servicoId);

    boolean existsByTipoServicoId(Integer tipoServicoId);

    boolean existsByPerfilId(Integer perfilId);
}
