package br.com.fiap.civia.infrastructure.dao;

import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.model.ConsultaTotem;
import br.com.fiap.civia.infrastructure.entity.ConsultaTotemEntity;
import br.com.fiap.civia.infrastructure.jpa.ConsultaTotemJpaRepository;
import br.com.fiap.civia.infrastructure.mapper.OperacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de T_CONSULTA_TOTEM. */
@Repository
@RequiredArgsConstructor
public class ConsultaTotemDAOImpl implements ConsultaTotemDAO {

    private final ConsultaTotemJpaRepository jpa;

    @Override
    public ConsultaTotem salvar(ConsultaTotem consulta) {
        ConsultaTotemEntity e = consulta.getId() == null ? new ConsultaTotemEntity()
                : jpa.findById(consulta.getId()).orElseGet(ConsultaTotemEntity::new);
        OperacaoPersistenceMapper.copiar(consulta, e);
        return OperacaoPersistenceMapper.paraDominio(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<ConsultaTotem> buscarPorId(Long id) {
        return jpa.findById(id).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public Optional<ConsultaTotem> buscarPorCodigoChegada(String codigo) {
        return jpa.findByCodigoChegada(codigo).map(OperacaoPersistenceMapper::paraDominio);
    }

    @Override
    public List<ConsultaTotem> listar(Long totemId) {
        return (totemId == null ? jpa.findAllByOrderByDataConsultaDescIdDesc()
                : jpa.findByTotemIdOrderByDataConsultaDescIdDesc(totemId))
                .stream().map(OperacaoPersistenceMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorCodigoChegada(String codigo) {
        return jpa.existsByCodigoChegada(codigo);
    }

    @Override
    public boolean existePorTotem(Long totemId) {
        return jpa.existsByTotemId(totemId);
    }

    @Override
    public boolean existePorServico(Long servicoId) {
        return jpa.existsByServicoEscolhidoId(servicoId);
    }

    @Override
    public boolean existePorTipo(Integer tipoServicoId) {
        return jpa.existsByTipoServicoId(tipoServicoId);
    }

    @Override
    public boolean existePorPerfil(Integer perfilId) {
        return jpa.existsByPerfilId(perfilId);
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
        jpa.flush();
    }
}
