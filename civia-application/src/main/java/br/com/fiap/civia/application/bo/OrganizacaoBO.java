package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.OrganizacaoRequest;
import br.com.fiap.civia.application.dto.OrganizacaoResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO das organizacoes da rede (T_ORGANIZACAO). Nome unico; nao exclui organizacao com servicos ou usuarios. */
@Service
@RequiredArgsConstructor
public class OrganizacaoBO {

    private final OrganizacaoDAO organizacaoDAO;
    private final ServicoDAO servicoDAO;
    private final UsuarioPainelDAO usuarioDAO;

    @Transactional(readOnly = true)
    public List<OrganizacaoResponse> listar(String tipo) {
        TipoOrganizacao filtro = Conversor.paraEnum(TipoOrganizacao.class, tipo, "tipo");
        return organizacaoDAO.listar(filtro).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrganizacaoResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public OrganizacaoResponse cadastrar(OrganizacaoRequest r) {
        Organizacao o = executar(() -> Organizacao.nova(r.nome(), tipo(r.tipo()), r.telefone(), r.email()));
        garantirNomeUnico(o.getNome(), null);
        return DtoMapper.paraResponse(organizacaoDAO.salvar(o));
    }

    @Transactional
    public OrganizacaoResponse atualizar(Long id, OrganizacaoRequest r) {
        Organizacao o = obter(id);
        executar(() -> o.atualizar(r.nome(), tipo(r.tipo()), r.telefone(), r.email()));
        garantirNomeUnico(o.getNome(), id);
        return DtoMapper.paraResponse(organizacaoDAO.salvar(o));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (servicoDAO.existePorOrganizacao(id) || usuarioDAO.existePorOrganizacao(id)) {
            throw new ConflitoException("A organizacao " + id + " possui servicos ou usuarios vinculados e nao pode ser excluida.");
        }
        organizacaoDAO.excluir(id);
    }

    Organizacao obter(Long id) {
        return organizacaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Organizacao " + id + " nao encontrada."));
    }

    private void garantirNomeUnico(String nome, Long idIgnorado) {
        if (organizacaoDAO.existePorNome(nome, idIgnorado)) {
            throw new ConflitoException("Ja existe uma organizacao com o nome " + nome + ".");
        }
    }

    private static TipoOrganizacao tipo(String tipo) {
        return Conversor.paraEnum(TipoOrganizacao.class, tipo, "tipo");
    }
}
