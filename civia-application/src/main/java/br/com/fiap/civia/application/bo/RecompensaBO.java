package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.RecompensaRequest;
import br.com.fiap.civia.application.dto.RecompensaResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.RecompensaDAO;
import br.com.fiap.civia.domain.dao.TrocaDAO;
import br.com.fiap.civia.domain.model.Recompensa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO do catalogo de recompensas (T_RECOMPENSA). Nome unico; item com trocas e desativado, nao excluido. */
@Service
@RequiredArgsConstructor
public class RecompensaBO {

    private final RecompensaDAO recompensaDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final TrocaDAO trocaDAO;

    @Transactional(readOnly = true)
    public List<RecompensaResponse> listar(Boolean ativo) {
        return recompensaDAO.listar(ativo).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public RecompensaResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public RecompensaResponse cadastrar(RecompensaRequest r) {
        garantirParceira(r.organizacaoParceiraId());
        Recompensa rec = executar(() -> Recompensa.nova(r.organizacaoParceiraId(), r.nome(), r.custoCreditos(),
                r.ativo() == null || r.ativo()));
        garantirNomeUnico(rec.getNome(), null);
        return DtoMapper.paraResponse(recompensaDAO.salvar(rec));
    }

    @Transactional
    public RecompensaResponse atualizar(Long id, RecompensaRequest r) {
        Recompensa rec = obter(id);
        garantirParceira(r.organizacaoParceiraId());
        executar(() -> rec.atualizar(r.organizacaoParceiraId(), r.nome(), r.custoCreditos(),
                r.ativo() == null ? rec.isAtivo() : r.ativo()));
        garantirNomeUnico(rec.getNome(), id);
        return DtoMapper.paraResponse(recompensaDAO.salvar(rec));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (trocaDAO.existePorRecompensa(id)) {
            throw new ConflitoException("A recompensa " + id + " possui trocas registradas. Desative-a (ativo = false) em vez de excluir.");
        }
        recompensaDAO.excluir(id);
    }

    private void garantirParceira(Long organizacaoId) {
        if (organizacaoDAO.buscarPorId(organizacaoId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Organizacao " + organizacaoId + " nao encontrada.");
        }
    }

    private void garantirNomeUnico(String nome, Long idIgnorado) {
        if (recompensaDAO.existePorNome(nome, idIgnorado)) {
            throw new ConflitoException("Ja existe uma recompensa com o nome " + nome + ".");
        }
    }

    private Recompensa obter(Long id) {
        return recompensaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa " + id + " nao encontrada."));
    }
}
