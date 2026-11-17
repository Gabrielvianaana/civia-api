package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Entrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** DAO de T_ENTREGA. */
public interface EntregaDAO {

    Entrega salvar(Entrega entrega);

    Optional<Entrega> buscarPorId(Long id);

    /** Filtros opcionais (nulo nao filtra). */
    List<Entrega> listar(Long cartaoId, Long postoId);

    /** Peso entregue pelo cartao no periodo [inicio, fim), ignorando a entrega {@code idIgnorado} (RN08). */
    BigDecimal somarPesoDoCartao(Long cartaoId, LocalDateTime inicio, LocalDateTime fim, Long idIgnorado);

    /** Peso entregue no posto depois de {@code desde} (nulo = desde sempre), ignorando {@code idIgnorado}. */
    BigDecimal somarPesoDoPosto(Long postoId, LocalDateTime desde, Long idIgnorado);

    boolean existePorCartao(Long cartaoId);

    boolean existePorPosto(Long postoId);

    boolean existePorMaterial(Integer materialId);

    void excluir(Long id);
}
