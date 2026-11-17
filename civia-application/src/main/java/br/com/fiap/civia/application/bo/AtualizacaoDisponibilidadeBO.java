package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeRequest;
import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.AtualizacaoDisponibilidadeDtoMapper;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.SituacaoServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO das atualizacoes de disponibilidade feitas pelas equipes (RF09 / US11).
 */
@Service
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeBO {

    /** Tolerancia para pequenas diferencas de relogio entre o painel e o servidor. */
    static final Duration TOLERANCIA_FUTURO = Duration.ofMinutes(5);

    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final ServicoDAO servicoDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<AtualizacaoDisponibilidadeResponse> listar(Long servicoId) {
        if (servicoId != null && !servicoDAO.existePorId(servicoId)) {
            throw servicoNaoEncontrado(servicoId);
        }
        return atualizacaoDAO.listar(servicoId).stream()
                .map(AtualizacaoDisponibilidadeDtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public AtualizacaoDisponibilidadeResponse buscar(Long id) {
        return AtualizacaoDisponibilidadeDtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public AtualizacaoDisponibilidadeResponse registrar(AtualizacaoDisponibilidadeRequest request) {
        Servico servico = obterServico(request.servicoId());
        SituacaoServico situacao = Conversor.paraEnum(SituacaoServico.class, request.situacao(), "situacao");
        LocalDateTime dataHora = dataHoraValida(request.dataHora());
        AtualizacaoDisponibilidade atualizacao = executar(() ->
                AtualizacaoDisponibilidade.registrar(servico, request.vagas(), situacao, dataHora));
        return AtualizacaoDisponibilidadeDtoMapper.paraResponse(atualizacaoDAO.salvar(atualizacao));
    }

    @Transactional
    public AtualizacaoDisponibilidadeResponse corrigir(Long id, AtualizacaoDisponibilidadeRequest request) {
        AtualizacaoDisponibilidade atualizacao = obter(id);
        Servico servico = obterServico(request.servicoId());
        SituacaoServico situacao = Conversor.paraEnum(SituacaoServico.class, request.situacao(), "situacao");
        LocalDateTime dataHora = dataHoraValida(request.dataHora());
        executar(() -> atualizacao.corrigir(servico, request.vagas(), situacao, dataHora));
        return AtualizacaoDisponibilidadeDtoMapper.paraResponse(atualizacaoDAO.salvar(atualizacao));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        atualizacaoDAO.excluir(id);
    }

    /** Sem data/hora informada, vale o momento do registro. Datas no futuro sao recusadas. */
    private LocalDateTime dataHoraValida(LocalDateTime informada) {
        LocalDateTime agora = LocalDateTime.now(clock);
        if (informada == null) {
            return agora.truncatedTo(ChronoUnit.SECONDS);
        }
        if (informada.isAfter(agora.plus(TOLERANCIA_FUTURO))) {
            throw new RegraNegocioException("A data/hora da atualizacao nao pode estar no futuro.");
        }
        return informada;
    }

    private AtualizacaoDisponibilidade obter(Long id) {
        return atualizacaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atualizacao " + id + " nao encontrada."));
    }

    private Servico obterServico(Long servicoId) {
        return servicoDAO.buscarPorId(servicoId).orElseThrow(() -> servicoNaoEncontrado(servicoId));
    }

    private static RecursoNaoEncontradoException servicoNaoEncontrado(Long servicoId) {
        return new RecursoNaoEncontradoException("Servico " + servicoId + " nao encontrado.");
    }
}
