package br.com.fiap.civia.infrastructure.jpa;

import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, Long> {

    List<ServicoEntity> findAllByOrderByIdAsc();

    List<ServicoEntity> findByTipoOrderByIdAsc(TipoServico tipo);

    List<ServicoEntity> findByAprovadoOrderByIdAsc(Boolean aprovado);

    List<ServicoEntity> findByTipoAndAprovadoOrderByIdAsc(TipoServico tipo, Boolean aprovado);
}
