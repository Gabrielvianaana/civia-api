package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.HorarioRequest;
import br.com.fiap.civia.application.dto.HorarioResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.HorarioDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.Servico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO dos horarios de funcionamento (T_HORARIO), sub-recurso de servico.
 * Regras: servico 24h nao tem horarios; (servico, dia, abertura) unico; faixas do mesmo dia nao se sobrepoem.
 */
@Service
@RequiredArgsConstructor
public class HorarioBO {

    private final HorarioDAO horarioDAO;
    private final ServicoDAO servicoDAO;

    @Transactional(readOnly = true)
    public List<HorarioResponse> listar(Long servicoId) {
        obterServico(servicoId);
        return horarioDAO.listarPorServico(servicoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public HorarioResponse buscar(Long servicoId, Long id) {
        return DtoMapper.paraResponse(obter(servicoId, id));
    }

    @Transactional
    public HorarioResponse cadastrar(Long servicoId, HorarioRequest r) {
        Servico servico = obterServico(servicoId);
        if (servico.isFunciona24h()) {
            throw new RegraNegocioException("O servico " + servicoId + " funciona 24h e nao precisa de horarios.");
        }
        Horario h = executar(() -> Horario.novo(servicoId, r.diaSemana(),
                Conversor.paraHora(r.horaAbertura(), "horaAbertura"), Conversor.paraHora(r.horaFechamento(), "horaFechamento")));
        validarConflitos(h);
        return DtoMapper.paraResponse(horarioDAO.salvar(h));
    }

    @Transactional
    public HorarioResponse atualizar(Long servicoId, Long id, HorarioRequest r) {
        Horario h = obter(servicoId, id);
        executar(() -> h.atualizar(r.diaSemana(), Conversor.paraHora(r.horaAbertura(), "horaAbertura"),
                Conversor.paraHora(r.horaFechamento(), "horaFechamento")));
        validarConflitos(h);
        return DtoMapper.paraResponse(horarioDAO.salvar(h));
    }

    @Transactional
    public void excluir(Long servicoId, Long id) {
        obter(servicoId, id);
        horarioDAO.excluir(id);
    }

    private void validarConflitos(Horario h) {
        if (horarioDAO.existe(h.getServicoId(), h.getDiaSemana(), h.getAbertura(), h.getId())) {
            throw new ConflitoException("Ja existe uma faixa de " + Horario.nomeDoDia(h.getDiaSemana())
                    + " abrindo as " + Conversor.deHora(h.getAbertura()) + " para este servico.");
        }
        boolean sobrepoe = horarioDAO.listarPorServico(h.getServicoId()).stream()
                .filter(outro -> !Objects.equals(outro.getId(), h.getId()) && outro.getDiaSemana() == h.getDiaSemana())
                .anyMatch(outro -> h.getAbertura().isBefore(outro.getFechamento()) && outro.getAbertura().isBefore(h.getFechamento()));
        if (sobrepoe) {
            throw new RegraNegocioException("A faixa " + Conversor.deHora(h.getAbertura()) + "-" + Conversor.deHora(h.getFechamento())
                    + " se sobrepoe a outra faixa de " + Horario.nomeDoDia(h.getDiaSemana()) + ".");
        }
    }

    private Horario obter(Long servicoId, Long id) {
        obterServico(servicoId);
        Horario h = horarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horario " + id + " nao encontrado."));
        if (!Objects.equals(h.getServicoId(), servicoId)) {
            throw new RecursoNaoEncontradoException("Horario " + id + " nao pertence ao servico " + servicoId + ".");
        }
        return h;
    }

    private Servico obterServico(Long servicoId) {
        return servicoDAO.buscarPorId(servicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico " + servicoId + " nao encontrado."));
    }
}
