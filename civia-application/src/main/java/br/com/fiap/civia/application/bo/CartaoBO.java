package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.CartaoAtualizacaoRequest;
import br.com.fiap.civia.application.dto.CartaoRequest;
import br.com.fiap.civia.application.dto.CartaoResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.CartaoDAO;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.TrocaDAO;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.StatusCartao;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO dos cartoes de reciclagem (T_CARTAO). Cartao anonimo com codigo aleatorio (RN07), emitido presencialmente
 * pelo Centro POP; a substituicao bloqueia o cartao perdido e transfere o saldo para o novo (RN12).
 */
@Service
@RequiredArgsConstructor
public class CartaoBO {

    /** Sem caracteres ambiguos (0/O, 1/I) para facilitar a leitura do codigo no cartao. */
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int MAX_TENTATIVAS_CODIGO = 20;

    private final CartaoDAO cartaoDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final EntregaDAO entregaDAO;
    private final TrocaDAO trocaDAO;
    private final Clock clock;
    private final RandomGenerator random = new SecureRandom();

    @Transactional(readOnly = true)
    public List<CartaoResponse> listar(String status) {
        StatusCartao filtro = Conversor.paraEnum(StatusCartao.class, status, "status");
        return cartaoDAO.listar(filtro).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public CartaoResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    /** Consulta do saldo pelo codigo impresso no cartao (feita no totem). */
    @Transactional(readOnly = true)
    public CartaoResponse buscarPorCodigo(String codigo) {
        return DtoMapper.paraResponse(obterPorCodigo(cartaoDAO, codigo));
    }

    @Transactional
    public CartaoResponse emitir(CartaoRequest r) {
        garantirEmissoraCentroPop(r.organizacaoEmissoraId());
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        if (r.cartaoAnteriorId() == null) {
            Cartao novo = executar(() -> Cartao.emitir(gerarCodigoUnico(), r.organizacaoEmissoraId(), agora));
            return DtoMapper.paraResponse(cartaoDAO.salvar(novo));
        }
        Cartao anterior = obter(r.cartaoAnteriorId());
        if (cartaoDAO.existeSubstituto(anterior.getId())) {
            throw new ConflitoException("O cartao " + anterior.getCodigo() + " ja foi substituido.");
        }
        Cartao novo = executar(() -> Cartao.substituir(anterior, gerarCodigoUnico(), r.organizacaoEmissoraId(), agora));
        cartaoDAO.salvar(anterior);
        return DtoMapper.paraResponse(cartaoDAO.salvar(novo));
    }

    @Transactional
    public CartaoResponse atualizar(Long id, CartaoAtualizacaoRequest r) {
        Cartao c = obter(id);
        garantirEmissoraCentroPop(r.organizacaoEmissoraId());
        StatusCartao status = Conversor.paraEnum(StatusCartao.class, r.status(), "status");
        if (status == StatusCartao.ATIVO && c.getStatus() == StatusCartao.BLOQUEADO && cartaoDAO.existeSubstituto(id)) {
            throw new RegraNegocioException("O cartao " + c.getCodigo() + " foi substituido e nao pode ser reativado (RN12).");
        }
        executar(() -> c.atualizar(r.organizacaoEmissoraId(), status));
        return DtoMapper.paraResponse(cartaoDAO.salvar(c));
    }

    @Transactional
    public void excluir(Long id) {
        Cartao c = obter(id);
        if (entregaDAO.existePorCartao(id) || trocaDAO.existePorCartao(id) || cartaoDAO.existeSubstituto(id)) {
            throw new ConflitoException("O cartao " + c.getCodigo()
                    + " possui entregas, trocas ou substituicao registradas. Use o status BLOQUEADO em vez de excluir.");
        }
        cartaoDAO.excluir(id);
    }

    /** Busca pelo codigo do cartao (sem diferenciar maiusculas e minusculas); usado tambem por entregas e trocas. */
    static Cartao obterPorCodigo(CartaoDAO cartaoDAO, String codigo) {
        String normalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        return cartaoDAO.buscarPorCodigo(normalizado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cartao " + normalizado + " nao encontrado."));
    }

    private void garantirEmissoraCentroPop(Long organizacaoId) {
        Organizacao o = organizacaoDAO.buscarPorId(organizacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Organizacao " + organizacaoId + " nao encontrada."));
        if (o.getTipo() != TipoOrganizacao.CENTRO_POP) {
            throw new RegraNegocioException("Cartoes sao emitidos e substituidos somente presencialmente no Centro POP (RN07 e RN12).");
        }
    }

    private String gerarCodigoUnico() {
        for (int i = 0; i < MAX_TENTATIVAS_CODIGO; i++) {
            StringBuilder sb = new StringBuilder(Cartao.TAMANHO_CODIGO);
            for (int j = 0; j < Cartao.TAMANHO_CODIGO; j++) {
                sb.append(ALFABETO_CODIGO.charAt(random.nextInt(ALFABETO_CODIGO.length())));
            }
            String codigo = sb.toString();
            if (!cartaoDAO.existePorCodigo(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("Nao foi possivel gerar um codigo de cartao unico.");
    }

    private Cartao obter(Long id) {
        return cartaoDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cartao " + id + " nao encontrado."));
    }
}
