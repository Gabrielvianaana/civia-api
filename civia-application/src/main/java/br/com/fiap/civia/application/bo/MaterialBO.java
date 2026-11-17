package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.MaterialRequest;
import br.com.fiap.civia.application.dto.MaterialResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.MaterialDAO;
import br.com.fiap.civia.domain.model.Material;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO dos materiais (T_MATERIAL). Nome unico; aceito gera creditos por kg, nao aceito tem 0 (RN09). */
@Service
@RequiredArgsConstructor
public class MaterialBO {

    private final MaterialDAO materialDAO;
    private final EntregaDAO entregaDAO;

    @Transactional(readOnly = true)
    public List<MaterialResponse> listar() {
        return materialDAO.listar().stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponse buscar(Integer id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public MaterialResponse cadastrar(MaterialRequest r) {
        Material m = executar(() -> Material.novo(r.nome(), r.creditosPorKg(), Boolean.TRUE.equals(r.aceito())));
        garantirNomeUnico(m.getNome(), null);
        return DtoMapper.paraResponse(materialDAO.salvar(m));
    }

    @Transactional
    public MaterialResponse atualizar(Integer id, MaterialRequest r) {
        Material m = obter(id);
        executar(() -> m.atualizar(r.nome(), r.creditosPorKg(), Boolean.TRUE.equals(r.aceito())));
        garantirNomeUnico(m.getNome(), id);
        return DtoMapper.paraResponse(materialDAO.salvar(m));
    }

    @Transactional
    public void excluir(Integer id) {
        obter(id);
        if (entregaDAO.existePorMaterial(id)) {
            throw new ConflitoException("O material " + id + " possui entregas registradas e nao pode ser excluido.");
        }
        materialDAO.excluir(id);
    }

    private Material obter(Integer id) {
        return materialDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Material " + id + " nao encontrado."));
    }

    private void garantirNomeUnico(String nome, Integer idIgnorado) {
        if (materialDAO.existePorNome(nome, idIgnorado)) {
            throw new ConflitoException("Ja existe um material com o nome " + nome + ".");
        }
    }
}
