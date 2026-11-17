package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.EntregaRequest;
import br.com.fiap.civia.application.dto.EntregaResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.CartaoDAO;
import br.com.fiap.civia.domain.dao.ColetaDAO;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.MaterialDAO;
import br.com.fiap.civia.domain.dao.PostoReciclagemDAO;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.Entrega;
import br.com.fiap.civia.domain.model.Material;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.StatusPosto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO das entregas de material (T_ENTREGA): so material aceito (RN09), limite diario por cartao (RN08),
 * capacidade do posto ate a proxima coleta e credito no cartao. O posto que enche passa a CHEIO.
 */
@Service
@RequiredArgsConstructor
public class EntregaBO {

    private final EntregaDAO entregaDAO;
    private final CartaoDAO cartaoDAO;
    private final PostoReciclagemDAO postoDAO;
    private final MaterialDAO materialDAO;
    private final ColetaDAO coletaDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<EntregaResponse> listar(Long cartaoId, Long postoId) {
        return entregaDAO.listar(cartaoId, postoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public EntregaResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public EntregaResponse registrar(EntregaRequest r) {
        Cartao cartao = CartaoBO.obterPorCodigo(cartaoDAO, r.codigoCartao());
        executar(cartao::garantirAtivo);
        PostoReciclagem posto = obterPosto(r.postoId());
        executar(posto::garantirRecebeEntregas);
        Material material = obterMaterial(r.materialId());
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        Entrega entrega = executar(() -> Entrega.registrar(cartao.getId(), posto.getId(), material, r.pesoKg(), agora));
        garantirLimiteDiario(cartao, posto, entrega.getPesoKg(), agora.toLocalDate(), null);
        BigDecimal acumulado = acumulado(posto.getId(), null);
        executar(() -> posto.garantirCabe(acumulado, entrega.getPesoKg()));

        executar(() -> cartao.creditar(entrega.getCreditosGerados()));
        cartaoDAO.salvar(cartao);
        Entrega salva = entregaDAO.salvar(entrega);
        posto.atualizarLotacao(acumulado.add(entrega.getPesoKg()));
        if (posto.getStatus() == StatusPosto.CHEIO) {
            postoDAO.salvar(posto);
        }
        return DtoMapper.paraResponse(salva);
    }

    /** Correcao de peso, material, cartao ou posto: estorna os creditos antigos e credita os novos. */
    @Transactional
    public EntregaResponse atualizar(Long id, EntregaRequest r) {
        Entrega entrega = obter(id);
        Cartao cartaoAnterior = obterCartao(entrega.getCartaoId());
        Cartao cartao = CartaoBO.obterPorCodigo(cartaoDAO, r.codigoCartao());
        Cartao cartaoNovo = Objects.equals(cartao.getId(), cartaoAnterior.getId()) ? cartaoAnterior : cartao;
        PostoReciclagem posto = obterPosto(r.postoId());
        boolean mudouPosto = !Objects.equals(posto.getId(), entrega.getPostoId());
        if (mudouPosto) {
            executar(posto::garantirRecebeEntregas);
        }
        Material material = obterMaterial(r.materialId());

        executar(() -> cartaoAnterior.debitar(entrega.getCreditosGerados()));
        executar(() -> entrega.corrigir(cartaoNovo.getId(), posto.getId(), material, r.pesoKg()));
        garantirLimiteDiario(cartaoNovo, posto, entrega.getPesoKg(), entrega.getDataEntrega().toLocalDate(), id);
        if (mudouPosto) {
            executar(() -> posto.garantirCabe(acumulado(posto.getId(), id), entrega.getPesoKg()));
        }
        executar(() -> cartaoNovo.creditar(entrega.getCreditosGerados()));

        cartaoDAO.salvar(cartaoAnterior);
        if (cartaoNovo != cartaoAnterior) {
            cartaoDAO.salvar(cartaoNovo);
        }
        return DtoMapper.paraResponse(entregaDAO.salvar(entrega));
    }

    /** Exclusao estorna os creditos; se ja foram trocados por recompensas, a exclusao e recusada. */
    @Transactional
    public void excluir(Long id) {
        Entrega entrega = obter(id);
        Cartao cartao = obterCartao(entrega.getCartaoId());
        executar(() -> cartao.debitar(entrega.getCreditosGerados()));
        cartaoDAO.salvar(cartao);
        entregaDAO.excluir(id);
    }

    /** RN08: o peso entregue pelo cartao no dia nao pode passar do limite diario do posto. */
    private void garantirLimiteDiario(Cartao cartao, PostoReciclagem posto, BigDecimal pesoKg, LocalDate dia, Long idIgnorado) {
        BigDecimal jaEntregue = entregaDAO.somarPesoDoCartao(cartao.getId(), dia.atStartOfDay(),
                dia.plusDays(1).atStartOfDay(), idIgnorado);
        BigDecimal limite = posto.getLimiteDiarioKg();
        if (jaEntregue.add(pesoKg).compareTo(limite) > 0) {
            BigDecimal restante = limite.subtract(jaEntregue).max(BigDecimal.ZERO);
            throw new RegraNegocioException("Limite diario de " + limite + " kg por cartao atingido (RN08): o cartao "
                    + cartao.getCodigo() + " ja entregou " + jaEntregue + " kg hoje e pode entregar mais " + restante + " kg.");
        }
    }

    private BigDecimal acumulado(Long postoId, Long entregaIgnorada) {
        return entregaDAO.somarPesoDoPosto(postoId, coletaDAO.ultimaColetaDoPosto(postoId).orElse(null), entregaIgnorada);
    }

    private Entrega obter(Long id) {
        return entregaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Entrega " + id + " nao encontrada."));
    }

    private Cartao obterCartao(Long id) {
        return cartaoDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cartao " + id + " nao encontrado."));
    }

    private PostoReciclagem obterPosto(Long id) {
        return postoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Posto de reciclagem " + id + " nao encontrado."));
    }

    private Material obterMaterial(Integer id) {
        return materialDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Material " + id + " nao encontrado."));
    }
}
