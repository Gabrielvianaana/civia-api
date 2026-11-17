package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.PerfilAtendidoRequest;
import br.com.fiap.civia.application.dto.PerfilAtendidoResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO dos perfis de publico atendido (T_PERFIL_ATENDIDO), usados no filtro de abrigo (US04). */
@Service
@RequiredArgsConstructor
public class PerfilAtendidoBO {

    private final PerfilAtendidoDAO perfilDAO;
    private final ServicoDAO servicoDAO;
    private final ConsultaTotemDAO consultaDAO;

    @Transactional(readOnly = true)
    public List<PerfilAtendidoResponse> listar() {
        return perfilDAO.listar().stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public PerfilAtendidoResponse buscar(Integer id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public PerfilAtendidoResponse cadastrar(PerfilAtendidoRequest r) {
        PerfilAtendido p = executar(() -> PerfilAtendido.novo(r.descricao()));
        garantirDescricaoUnica(p.getDescricao(), null);
        return DtoMapper.paraResponse(perfilDAO.salvar(p));
    }

    @Transactional
    public PerfilAtendidoResponse atualizar(Integer id, PerfilAtendidoRequest r) {
        PerfilAtendido p = obter(id);
        executar(() -> p.atualizar(r.descricao()));
        garantirDescricaoUnica(p.getDescricao(), id);
        return DtoMapper.paraResponse(perfilDAO.salvar(p));
    }

    @Transactional
    public void excluir(Integer id) {
        obter(id);
        if (servicoDAO.existePorPerfil(id) || consultaDAO.existePorPerfil(id)) {
            throw new ConflitoException("O perfil " + id + " esta em uso por servicos ou consultas e nao pode ser excluido.");
        }
        perfilDAO.excluir(id);
    }

    PerfilAtendido obter(Integer id) {
        return perfilDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil " + id + " nao encontrado."));
    }

    private void garantirDescricaoUnica(String descricao, Integer idIgnorado) {
        if (perfilDAO.existePorDescricao(descricao, idIgnorado)) {
            throw new ConflitoException("Ja existe um perfil com a descricao " + descricao + ".");
        }
    }
}
