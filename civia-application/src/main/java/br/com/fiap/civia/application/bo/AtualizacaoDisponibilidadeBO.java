package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeRequest;
import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.OrigemAtualizacao;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.SituacaoServico;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import br.com.fiap.civia.domain.model.UsuarioPainel;
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
 * BO das atualizacoes de disponibilidade (T_ATUALIZACAO), feitas pelo painel (US11) ou pela Central de Vagas (US12).
 * Aplica a RN03 na gravacao: para abrigo recebe a quantidade de vagas e grava a faixa correspondente.
 */
@Service
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeBO {

    /** Tolerancia para pequenas diferencas de relogio entre o painel e o servidor. */
    static final Duration TOLERANCIA_FUTURO = Duration.ofMinutes(5);

    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final ServicoDAO servicoDAO;
    private final UsuarioPainelDAO usuarioDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<AtualizacaoDisponibilidadeResponse> listar(Long servicoId) {
        if (servicoId != null && !servicoDAO.existePorId(servicoId)) {
            throw new RecursoNaoEncontradoException("Servico " + servicoId + " nao encontrado.");
        }
        return atualizacaoDAO.listar(servicoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public AtualizacaoDisponibilidadeResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public AtualizacaoDisponibilidadeResponse registrar(AtualizacaoDisponibilidadeRequest r) {
        Dados d = interpretar(r);
        AtualizacaoDisponibilidade a = executar(() -> AtualizacaoDisponibilidade.registrar(
                d.servico(), r.usuarioId(), d.status(), d.vagas(), d.origem(), d.dataHora()));
        return DtoMapper.paraResponse(atualizacaoDAO.salvar(a));
    }

    @Transactional
    public AtualizacaoDisponibilidadeResponse corrigir(Long id, AtualizacaoDisponibilidadeRequest r) {
        AtualizacaoDisponibilidade a = obter(id);
        Dados d = interpretar(r);
        executar(() -> a.corrigir(d.servico(), r.usuarioId(), d.status(), d.vagas(), d.origem(), d.dataHora()));
        return DtoMapper.paraResponse(atualizacaoDAO.salvar(a));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        atualizacaoDAO.excluir(id);
    }

    /**
     * Converte a requisicao no status a gravar.
     * Abrigo: exige vagas e grava a faixa (RN03). Demais tipos: exige a situacao do painel;
     * LOTADO grava 0 vagas (CHECK ck_atu_faixa).
     */
    private Dados interpretar(AtualizacaoDisponibilidadeRequest r) {
        Servico servico = servicoDAO.buscarPorId(r.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico " + r.servicoId() + " nao encontrado."));
        OrigemAtualizacao origem = r.origem() == null || r.origem().isBlank() ? OrigemAtualizacao.PAINEL
                : Conversor.paraEnum(OrigemAtualizacao.class, r.origem(), "origem");
        SituacaoServico situacao = Conversor.paraEnum(SituacaoServico.class, r.situacao(), "situacao");

        if (origem == OrigemAtualizacao.PAINEL && r.usuarioId() != null) {
            UsuarioPainel usuario = usuarioDAO.buscarPorId(r.usuarioId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario " + r.usuarioId() + " nao encontrado."));
            executar(() -> usuario.garantirPodeAtualizar(servico));
        }

        StatusDisponibilidade status;
        Integer vagas;
        if (servico.ehAbrigo()) {
            if (r.vagas() == null) {
                throw new RegraNegocioException("Para abrigo, informe a quantidade de vagas disponiveis.");
            }
            if (situacao != null) {
                throw new RegraNegocioException("Para abrigo, a disponibilidade e informada por vagas, nao por situacao.");
            }
            status = DisponibilidadeBO.faixaDeVagas(r.vagas());
            vagas = r.vagas();
        } else {
            if (r.vagas() != null) {
                throw new RegraNegocioException("Vagas so podem ser informadas para abrigos. Para "
                        + servico.getTipo().getNome() + ", informe a situacao (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).");
            }
            if (situacao == null) {
                throw new RegraNegocioException("Informe a situacao do servico (ABERTO, FILA_GRANDE, LOTADO ou FECHADO).");
            }
            status = StatusDisponibilidade.de(situacao);
            vagas = status == StatusDisponibilidade.LOTADO ? 0 : null;
        }
        return new Dados(servico, origem, status, vagas, dataHoraValida(r.dataHora()));
    }

    /** Sem data/hora informada, vale o momento do registro. Datas no futuro sao recusadas. */
    private LocalDateTime dataHoraValida(LocalDateTime informada) {
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        if (informada == null) {
            return agora;
        }
        if (informada.isAfter(agora.plus(TOLERANCIA_FUTURO))) {
            throw new RegraNegocioException("A data/hora da atualizacao nao pode estar no futuro.");
        }
        return informada.truncatedTo(ChronoUnit.SECONDS);
    }

    private AtualizacaoDisponibilidade obter(Long id) {
        return atualizacaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atualizacao " + id + " nao encontrada."));
    }

    private record Dados(Servico servico, OrigemAtualizacao origem, StatusDisponibilidade status, Integer vagas,
                         LocalDateTime dataHora) {
    }
}
