package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.CartaoDAO;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.StatusCartao;
import br.com.fiap.civia.infrastructure.entity.CartaoEntity;
import br.com.fiap.civia.infrastructure.jpa.CartaoJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.ReciclagemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_CARTAO. */
@Repository
@RequiredArgsConstructor
public class CartaoDAOImpl implements CartaoDAO {

    private final CartaoJpaRepository jpa;

    @Override
    public Cartao salvar(Cartao dominio) {
        CartaoEntity e = dominio.getId() == null ? new CartaoEntity()
                : jpa.findById(dominio.getId()).orElseGet(CartaoEntity::new);
        ReciclagemPersistenceMapper.copiar(dominio, e);
        return ReciclagemPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Cartao> buscarPorId(Long id) {
        return jpa.findById(id).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public Optional<Cartao> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(ReciclagemPersistenceMapper::paraDominio);
    }

    @Override
    public List<Cartao> listar(StatusCartao status) {
        return (status == null ? jpa.findAllByOrderByIdAsc() : jpa.findByStatusOrderByIdAsc(status))
                .stream().map(ReciclagemPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return jpa.existsByCodigo(codigo);
    }

    @Override
    public boolean existeSubstituto(Long cartaoAnteriorId) {
        return jpa.existsByCartaoAnteriorId(cartaoAnteriorId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
