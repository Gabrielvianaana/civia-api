package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import br.com.fiap.civia.infrastructure.entity.UsuarioPainelEntity;
import br.com.fiap.civia.infrastructure.jpa.UsuarioPainelJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.OperacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_USUARIO_PAINEL. */
@Repository
@RequiredArgsConstructor
public class UsuarioPainelDAOImpl implements UsuarioPainelDAO {

    private final UsuarioPainelJpaRepository jpa;

    @Override
    public UsuarioPainel salvar(UsuarioPainel usuario) {
        UsuarioPainelEntity e = usuario.getId() == null ? new UsuarioPainelEntity()
                : jpa.findById(usuario.getId()).orElseGet(UsuarioPainelEntity::new);
        OperacaoPersistenceMapper.copiar(usuario, e);
        return OperacaoPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<UsuarioPainel> buscarPorId(Long id) {
        return jpa.findById(id).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public List<UsuarioPainel> listar(Long organizacaoId) {
        return (organizacaoId == null ? jpa.findAllByOrderByIdAsc() : jpa.findByOrganizacaoIdOrderByIdAsc(organizacaoId))
                .stream().map(OperacaoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorEmail(String email, Long idIgnorado) {
        return idIgnorado == null ? jpa.existsByEmailIgnoreCase(email) : jpa.existsByEmailIgnoreCaseAndIdNot(email, idIgnorado);
    }

    @Override
    public boolean existePorOrganizacao(Long organizacaoId) {
        return jpa.existsByOrganizacaoId(organizacaoId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
