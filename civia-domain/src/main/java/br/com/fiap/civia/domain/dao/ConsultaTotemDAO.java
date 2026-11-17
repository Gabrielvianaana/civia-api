package br.com.fiap.civia.domain.dao;

import br.com.fiap.civia.domain.model.ConsultaTotem;

import java.util.List;
import java.util.Optional;

/** DAO de T_CONSULTA_TOTEM. */
public interface ConsultaTotemDAO {

    ConsultaTotem salvar(ConsultaTotem consulta);

    Optional<ConsultaTotem> buscarPorId(Long id);

    Optional<ConsultaTotem> buscarPorCodigoChegada(String codigo);

    /** Da mais recente para a mais antiga; {@code totemId} nulo lista todas. */
    List<ConsultaTotem> listar(Long totemId);

    boolean existePorCodigoChegada(String codigo);

    boolean existePorTotem(Long totemId);

    boolean existePorServico(Long servicoId);

    boolean existePorTipo(Integer tipoServicoId);

    boolean existePorPerfil(Integer perfilId);

    void excluir(Long id);
}
