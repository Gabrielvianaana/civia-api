package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Coleta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** DAO de T_COLETA. */
public interface ColetaDAO {

    Coleta salvar(Coleta coleta);

    Optional<Coleta> buscarPorId(Long id);

    /** {@code postoId} nulo lista todas. */
    List<Coleta> listar(Long postoId);

    /** Data da coleta mais recente do posto (o material acumulado conta a partir dela). */
    Optional<LocalDateTime> ultimaColetaDoPosto(Long postoId);

    boolean existePorPosto(Long postoId);

    void excluir(Long id);
}
