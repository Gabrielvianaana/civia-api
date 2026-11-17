package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.Totem;

import java.util.List;
import java.util.Optional;

/** DAO de totens. */
public interface TotemDAO {

    Totem salvar(Totem totem);

    Optional<Totem> buscarPorId(Long id);

    List<Totem> listar();

    boolean existePorIdentificacao(String identificacao);

    /** Verifica duplicidade de identificacao ignorando o proprio totem (usado na atualizacao). */
    boolean existePorIdentificacaoEmOutroTotem(String identificacao, Long idTotem);

    long contar();

    void excluir(Long id);
}
