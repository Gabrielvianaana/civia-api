package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ColetaRequest;
import br.com.fiap.civia.application.dto.ColetaResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.ColetaDAO;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.PostoReciclagemDAO;
import br.com.fiap.civia.domain.model.Coleta;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.StatusPosto;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO das coletas (T_COLETA): feitas pela cooperativa responsavel pelo posto. Sem peso informado, registra o
 * material acumulado desde a ultima coleta; o posto CHEIO volta a ATIVO.
 */
@Service
@RequiredArgsConstructor
public class ColetaBO {

    private final ColetaDAO coletaDAO;
    private final PostoReciclagemDAO postoDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final EntregaDAO entregaDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<ColetaResponse> listar(Long postoId) {
        return coletaDAO.listar(postoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public ColetaResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public ColetaResponse registrar(ColetaRequest r) {
        PostoReciclagem posto = obterPosto(r.postoId());
        garantirCooperativaDoPosto(posto, r.cooperativaId());
        BigDecimal peso = r.pesoTotalKg();
        if (peso == null) {
            peso = entregaDAO.somarPesoDoPosto(posto.getId(), coletaDAO.ultimaColetaDoPosto(posto.getId()).orElse(null), null);
            if (peso.signum() == 0) {
                throw new RegraNegocioException("Nao ha material acumulado no posto " + posto.getId() + " para coletar.");
            }
        }
        BigDecimal pesoFinal = peso;
        LocalDateTime data = r.dataColeta() == null ? agora() : r.dataColeta().truncatedTo(ChronoUnit.SECONDS);
        Coleta coleta = executar(() -> Coleta.registrar(posto.getId(), r.cooperativaId(), pesoFinal, data));
        Coleta salva = coletaDAO.salvar(coleta);
        if (posto.getStatus() == StatusPosto.CHEIO) {
            posto.liberarAposColeta();
            postoDAO.salvar(posto);
        }
        return DtoMapper.paraResponse(salva);
    }

    @Transactional
    public ColetaResponse atualizar(Long id, ColetaRequest r) {
        Coleta coleta = obter(id);
        PostoReciclagem posto = obterPosto(r.postoId());
        garantirCooperativaDoPosto(posto, r.cooperativaId());
        executar(() -> coleta.atualizar(posto.getId(), r.cooperativaId(),
                r.pesoTotalKg() == null ? coleta.getPesoTotalKg() : r.pesoTotalKg(),
                r.dataColeta() == null ? null : r.dataColeta().truncatedTo(ChronoUnit.SECONDS)));
        return DtoMapper.paraResponse(coletaDAO.salvar(coleta));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        coletaDAO.excluir(id);
    }

    private void garantirCooperativaDoPosto(PostoReciclagem posto, Long cooperativaId) {
        Organizacao cooperativa = organizacaoDAO.buscarPorId(cooperativaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Organizacao " + cooperativaId + " nao encontrada."));
        if (cooperativa.getTipo() != TipoOrganizacao.COOPERATIVA || !Objects.equals(cooperativa.getId(), posto.getCooperativaId())) {
            throw new RegraNegocioException("A coleta do posto " + posto.getId()
                    + " deve ser feita pela cooperativa responsavel por ele (organizacao " + posto.getCooperativaId() + ").");
        }
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

    private Coleta obter(Long id) {
        return coletaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Coleta " + id + " nao encontrada."));
    }

    private PostoReciclagem obterPosto(Long id) {
        return postoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Posto de reciclagem " + id + " nao encontrado."));
    }
}
