package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.infrastructure.entity.ServicoEntity;
import br.com.fiap.civia.infrastructure.jpa.PerfilAtendidoJpaRepository;
import br.com.fiap.civia.infrastructure.jpa.ServicoJpaRepository;
import br.com.fiap.civia.infrastructure.jpa.TipoServicoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ServicoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** DAO de T_SERVICO e T_SERVICO_PERFIL. */
@Repository
@RequiredArgsConstructor
public class ServicoDAOImpl implements ServicoDAO {

    private final ServicoJpaRepository jpa;
    private final TipoServicoJpaRepository tipoJpa;
    private final PerfilAtendidoJpaRepository perfilJpa;

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity e = servico.getId() == null ? new ServicoEntity()
                : jpa.findById(servico.getId()).orElseGet(ServicoEntity::new);
        ServicoPersistenceMapper.copiar(servico, e);
        e.setTipo(tipoJpa.getReferenceById(servico.getTipo().getId()));
        e.getPerfis().clear();
        e.getPerfis().addAll(servico.getPerfis().stream()
                .map(PerfilAtendido::getId)
                .map(perfilJpa::getReferenceById)
                .collect(Collectors.toSet()));
        jpa.saveAndFlush(e);
        return buscarPorId(e.getId()).orElseThrow();
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return jpa.findById(id).map(ServicoPersistenceMapper::paraDominio);
    }

    @Override
    public List<Servico> listar(Integer tipoServicoId, StatusAprovacao statusAprovacao, Long organizacaoId) {
        return jpa.filtrar(tipoServicoId, statusAprovacao, organizacaoId).stream()
                .distinct()
                .map(ServicoPersistenceMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existePorOrganizacao(Long organizacaoId) {
        return jpa.existsByOrganizacaoId(organizacaoId);
    }

    @Override
    public boolean existePorTipo(Integer tipoServicoId) {
        return jpa.existsByTipoId(tipoServicoId);
    }

    @Override
    public boolean existePorPerfil(Integer perfilId) {
        return jpa.existsByPerfisId(perfilId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
