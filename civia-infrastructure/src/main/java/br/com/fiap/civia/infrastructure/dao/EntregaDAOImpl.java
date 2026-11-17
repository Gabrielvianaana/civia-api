package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.model.Entrega;
import br.com.fiap.civia.infrastructure.entity.EntregaEntity;
import br.com.fiap.civia.infrastructure.jpa.EntregaJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** DAO de T_ENTREGA. */
@Repository
@RequiredArgsConstructor
public class EntregaDAOImpl implements EntregaDAO {

    private final EntregaJpaRepository jpa;

    @Override
    public Entrega salvar(Entrega dominio) {
        EntregaEntity e = dominio.getId() == null ? new EntregaEntity()
                : jpa.findById(dominio.getId()).orElseGet(EntregaEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Entrega> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    /** Ids gerados pela sequencia sao positivos: -1 significa "nao ignorar nenhuma entrega". */
    private static final long NENHUMA = -1L;

    @Override
    public List<Entrega> listar(Long cartaoId, Long postoId) {
        return jpa.filtrar(cartaoId, postoId).stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public BigDecimal somarPesoDoCartao(Long cartaoId, LocalDateTime inicio, LocalDateTime fim, Long idIgnorado) {
        return duasCasas(jpa.somarPesoDoCartao(cartaoId, inicio, fim, idIgnorado == null ? NENHUMA : idIgnorado));
    }

    @Override
    public BigDecimal somarPesoDoPosto(Long postoId, LocalDateTime desde, Long idIgnorado) {
        long ignorar = idIgnorado == null ? NENHUMA : idIgnorado;
        return duasCasas(desde == null ? jpa.somarPesoDoPosto(postoId, ignorar) : jpa.somarPesoDoPostoDesde(postoId, desde, ignorar));
    }

    /** Mesma escala da coluna NUMBER(p,2). */
    private static BigDecimal duasCasas(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean existePorCartao(Long cartaoId) {
        return jpa.existsByCartaoId(cartaoId);
    }

    @Override
    public boolean existePorPosto(Long postoId) {
        return jpa.existsByPostoId(postoId);
    }

    @Override
    public boolean existePorMaterial(Integer materialId) {
        return jpa.existsByMaterialId(materialId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
