package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.PostoReciclagemRequest;
import br.com.fiap.civia.application.dto.PostoReciclagemResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.domain.dao.ColetaDAO;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.PostoReciclagemDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.StatusPosto;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO dos postos de reciclagem (T_POSTO_RECICLAGEM). Um posto por totem, atendido por uma organizacao do tipo
 * COOPERATIVA. A resposta mostra o material acumulado desde a ultima coleta.
 */
@Service
@RequiredArgsConstructor
public class PostoReciclagemBO {

    private final PostoReciclagemDAO postoDAO;
    private final TotemDAO totemDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final EntregaDAO entregaDAO;
    private final ColetaDAO coletaDAO;

    @Transactional(readOnly = true)
    public List<PostoReciclagemResponse> listar(String status) {
        StatusPosto filtro = Conversor.paraEnum(StatusPosto.class, status, "status");
        return postoDAO.listar(filtro).stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public PostoReciclagemResponse buscar(Long id) {
        return paraResponse(obter(id));
    }

    @Transactional
    public PostoReciclagemResponse cadastrar(PostoReciclagemRequest r) {
        validarReferencias(r);
        PostoReciclagem p = executar(() -> PostoReciclagem.novo(r.totemId(), r.cooperativaId(), r.capacidadeKg(),
                r.limiteDiarioKg(), status(r.status())));
        garantirUmPostoPorTotem(p.getTotemId(), null);
        return paraResponse(postoDAO.salvar(p));
    }

    @Transactional
    public PostoReciclagemResponse atualizar(Long id, PostoReciclagemRequest r) {
        PostoReciclagem p = obter(id);
        validarReferencias(r);
        executar(() -> p.atualizar(r.totemId(), r.cooperativaId(), r.capacidadeKg(), r.limiteDiarioKg(), status(r.status())));
        garantirUmPostoPorTotem(p.getTotemId(), id);
        return paraResponse(postoDAO.salvar(p));
    }

    /** Posto com entregas ou coletas guarda historico de creditos: use o status MANUTENCAO. */
    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (entregaDAO.existePorPosto(id) || coletaDAO.existePorPosto(id)) {
            throw new ConflitoException("O posto " + id + " possui entregas ou coletas registradas. Use o status MANUTENCAO em vez de excluir.");
        }
        postoDAO.excluir(id);
    }

    private PostoReciclagemResponse paraResponse(PostoReciclagem p) {
        return new PostoReciclagemResponse(p.getId(), p.getTotemId(), p.getCooperativaId(), p.getCapacidadeKg(),
                p.getLimiteDiarioKg(), p.getStatus().name(),
                entregaDAO.somarPesoDoPosto(p.getId(), coletaDAO.ultimaColetaDoPosto(p.getId()).orElse(null), null));
    }

    private void validarReferencias(PostoReciclagemRequest r) {
        if (totemDAO.buscarPorId(r.totemId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Totem " + r.totemId() + " nao encontrado.");
        }
        Organizacao cooperativa = organizacaoDAO.buscarPorId(r.cooperativaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Organizacao " + r.cooperativaId() + " nao encontrada."));
        if (cooperativa.getTipo() != TipoOrganizacao.COOPERATIVA) {
            throw new RegraNegocioException("O posto deve ser atendido por uma organizacao do tipo COOPERATIVA.");
        }
    }

    private void garantirUmPostoPorTotem(Long totemId, Long idIgnorado) {
        if (postoDAO.existePorTotem(totemId, idIgnorado)) {
            throw new ConflitoException("O totem " + totemId + " ja possui um posto de reciclagem.");
        }
    }

    private PostoReciclagem obter(Long id) {
        return postoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Posto de reciclagem " + id + " nao encontrado."));
    }

    private static StatusPosto status(String status) {
        return Conversor.paraEnum(StatusPosto.class, status, "status");
    }
}
