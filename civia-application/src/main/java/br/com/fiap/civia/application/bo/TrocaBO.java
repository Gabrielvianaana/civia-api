package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.TrocaRequest;
import br.com.fiap.civia.application.dto.TrocaResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.CartaoDAO;
import br.com.fiap.civia.domain.dao.RecompensaDAO;
import br.com.fiap.civia.domain.dao.TrocaDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.Recompensa;
import br.com.fiap.civia.domain.model.Troca;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO das trocas (T_TROCA): os creditos so viram itens do catalogo, nunca dinheiro, e o saldo nunca fica
 * negativo (RN11). A troca e registrada por um usuario ativo da equipe ou gestor.
 */
@Service
@RequiredArgsConstructor
public class TrocaBO {

    private final TrocaDAO trocaDAO;
    private final CartaoDAO cartaoDAO;
    private final RecompensaDAO recompensaDAO;
    private final UsuarioPainelDAO usuarioDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<TrocaResponse> listar(Long cartaoId) {
        return trocaDAO.listar(cartaoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public TrocaResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public TrocaResponse registrar(TrocaRequest r) {
        Cartao cartao = CartaoBO.obterPorCodigo(cartaoDAO, r.codigoCartao());
        Recompensa recompensa = obterRecompensa(r.recompensaId());
        UsuarioPainel usuario = obterUsuario(r.usuarioId());
        executar(usuario::garantirPodeRegistrarTroca);
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        Troca troca = executar(() -> Troca.registrar(cartao.getId(), recompensa, usuario.getId(), agora));
        executar(() -> cartao.debitar(troca.getCreditosUsados()));
        cartaoDAO.salvar(cartao);
        return DtoMapper.paraResponse(trocaDAO.salvar(troca));
    }

    /** Correcao da troca: devolve os creditos antigos e debita o custo da recompensa informada. */
    @Transactional
    public TrocaResponse atualizar(Long id, TrocaRequest r) {
        Troca troca = obter(id);
        Cartao cartaoAnterior = obterCartao(troca.getCartaoId());
        Cartao cartao = CartaoBO.obterPorCodigo(cartaoDAO, r.codigoCartao());
        Cartao cartaoNovo = Objects.equals(cartao.getId(), cartaoAnterior.getId()) ? cartaoAnterior : cartao;
        Recompensa recompensa = obterRecompensa(r.recompensaId());
        UsuarioPainel usuario = obterUsuario(r.usuarioId());
        executar(usuario::garantirPodeRegistrarTroca);

        executar(() -> cartaoAnterior.creditar(troca.getCreditosUsados()));
        executar(() -> troca.corrigir(cartaoNovo.getId(), recompensa, usuario.getId()));
        executar(() -> cartaoNovo.debitar(troca.getCreditosUsados()));

        cartaoDAO.salvar(cartaoAnterior);
        if (cartaoNovo != cartaoAnterior) {
            cartaoDAO.salvar(cartaoNovo);
        }
        return DtoMapper.paraResponse(trocaDAO.salvar(troca));
    }

    /** Cancelamento da troca: os creditos voltam para o cartao. */
    @Transactional
    public void excluir(Long id) {
        Troca troca = obter(id);
        Cartao cartao = obterCartao(troca.getCartaoId());
        executar(() -> cartao.creditar(troca.getCreditosUsados()));
        cartaoDAO.salvar(cartao);
        trocaDAO.excluir(id);
    }

    private Troca obter(Long id) {
        return trocaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Troca " + id + " nao encontrada."));
    }

    private Cartao obterCartao(Long id) {
        return cartaoDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cartao " + id + " nao encontrado."));
    }

    private Recompensa obterRecompensa(Long id) {
        return recompensaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa " + id + " nao encontrada."));
    }

    private UsuarioPainel obterUsuario(Long id) {
        return usuarioDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado."));
    }
}
