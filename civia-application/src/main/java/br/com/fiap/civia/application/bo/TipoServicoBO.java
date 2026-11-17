package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.TipoServicoRequest;
import br.com.fiap.civia.application.dto.TipoServicoResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.model.TipoServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO dos tipos de servico (T_TIPO_SERVICO), exibidos na tela inicial do totem (RF01). */
@Service
@RequiredArgsConstructor
public class TipoServicoBO {

    private final TipoServicoDAO tipoDAO;
    private final ServicoDAO servicoDAO;
    private final ConsultaTotemDAO consultaDAO;

    @Transactional(readOnly = true)
    public List<TipoServicoResponse> listar() {
        return tipoDAO.listar().stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public TipoServicoResponse buscar(Integer id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public TipoServicoResponse cadastrar(TipoServicoRequest r) {
        TipoServico t = executar(() -> TipoServico.novo(r.nome(), r.icone()));
        garantirNomeUnico(t.getNome(), null);
        return DtoMapper.paraResponse(tipoDAO.salvar(t));
    }

    @Transactional
    public TipoServicoResponse atualizar(Integer id, TipoServicoRequest r) {
        TipoServico t = obter(id);
        executar(() -> t.atualizar(r.nome(), r.icone()));
        garantirNomeUnico(t.getNome(), id);
        return DtoMapper.paraResponse(tipoDAO.salvar(t));
    }

    @Transactional
    public void excluir(Integer id) {
        obter(id);
        if (servicoDAO.existePorTipo(id) || consultaDAO.existePorTipo(id)) {
            throw new ConflitoException("O tipo de servico " + id + " esta em uso por servicos ou consultas e nao pode ser excluido.");
        }
        tipoDAO.excluir(id);
    }

    TipoServico obter(Integer id) {
        return tipoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de servico " + id + " nao encontrado."));
    }

    private void garantirNomeUnico(String nome, Integer idIgnorado) {
        if (tipoDAO.existePorNome(nome, idIgnorado)) {
            throw new ConflitoException("Ja existe um tipo de servico com o nome " + nome + ".");
        }
    }
}
