package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ConsultaTotemRequest;
import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.ConsultaTotem;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.Totem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.random.RandomGenerator;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO das consultas registradas nos totens (T_CONSULTA_TOTEM), base dos indicadores de uso (US15)
 * e do registro de chegada pelo codigo impresso (RF12 / US14). Nenhum dado pessoal e armazenado (RNF07).
 */
@Service
@RequiredArgsConstructor
public class ConsultaTotemBO {

    /** Sem caracteres ambiguos (0/O, 1/I) para facilitar a digitacao do codigo impresso. */
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int MAX_TENTATIVAS_CODIGO = 20;

    private final ConsultaTotemDAO consultaDAO;
    private final TotemDAO totemDAO;
    private final TipoServicoDAO tipoDAO;
    private final PerfilAtendidoDAO perfilDAO;
    private final ServicoDAO servicoDAO;
    private final Clock clock;
    private final RandomGenerator random = new SecureRandom();

    @Transactional(readOnly = true)
    public List<ConsultaTotemResponse> listar(Long totemId) {
        return consultaDAO.listar(totemId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaTotemResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public ConsultaTotemResponse registrar(ConsultaTotemRequest r) {
        Totem totem = totemDAO.buscarPorId(r.totemId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Totem " + r.totemId() + " nao encontrado."));
        executar(totem::garantirDisponivelParaConsulta);
        validarReferencias(r);
        boolean imprimiu = Boolean.TRUE.equals(r.imprimiuRota());
        ConsultaTotem c = executar(() -> ConsultaTotem.registrar(r.totemId(), r.tipoServicoId(), r.perfilId(),
                r.servicoEscolhidoId(), agora(), imprimiu, Boolean.TRUE.equals(r.pediuAbordagem()),
                imprimiu ? gerarCodigoUnico() : null));
        return DtoMapper.paraResponse(consultaDAO.salvar(c));
    }

    @Transactional
    public ConsultaTotemResponse atualizar(Long id, ConsultaTotemRequest r) {
        ConsultaTotem c = obter(id);
        if (!totemDAO.buscarPorId(r.totemId()).isPresent()) {
            throw new RecursoNaoEncontradoException("Totem " + r.totemId() + " nao encontrado.");
        }
        validarReferencias(r);
        boolean imprimiu = Boolean.TRUE.equals(r.imprimiuRota());
        String codigoNovo = imprimiu && c.getCodigoChegada() == null ? gerarCodigoUnico() : null;
        executar(() -> c.atualizar(r.totemId(), r.tipoServicoId(), r.perfilId(), r.servicoEscolhidoId(), imprimiu,
                Boolean.TRUE.equals(r.pediuAbordagem()), codigoNovo));
        return DtoMapper.paraResponse(consultaDAO.salvar(c));
    }

    /** US14: o servico digita o codigo do papel e a chegada e registrada. */
    @Transactional
    public ConsultaTotemResponse registrarChegada(String codigo) {
        String normalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        ConsultaTotem c = consultaDAO.buscarPorCodigoChegada(normalizado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Codigo de chegada " + normalizado + " nao encontrado."));
        executar(() -> c.registrarChegada(agora()));
        return DtoMapper.paraResponse(consultaDAO.salvar(c));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        consultaDAO.excluir(id);
    }

    private void validarReferencias(ConsultaTotemRequest r) {
        if (tipoDAO.buscarPorId(r.tipoServicoId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Tipo de servico " + r.tipoServicoId() + " nao encontrado.");
        }
        if (r.perfilId() != null && perfilDAO.buscarPorId(r.perfilId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Perfil " + r.perfilId() + " nao encontrado.");
        }
        if (r.servicoEscolhidoId() != null) {
            Servico s = servicoDAO.buscarPorId(r.servicoEscolhidoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Servico " + r.servicoEscolhidoId() + " nao encontrado."));
            if (!s.estaAprovado()) {
                throw new RegraNegocioException("O servico " + s.getId() + " nao esta aprovado e nao aparece nos totens (RN06).");
            }
            if (!Objects.equals(s.getTipo().getId(), r.tipoServicoId())) {
                throw new RegraNegocioException("O servico escolhido nao e do tipo consultado.");
            }
        }
    }

    private String gerarCodigoUnico() {
        for (int i = 0; i < MAX_TENTATIVAS_CODIGO; i++) {
            StringBuilder sb = new StringBuilder(ConsultaTotem.TAMANHO_CODIGO);
            for (int j = 0; j < ConsultaTotem.TAMANHO_CODIGO; j++) {
                sb.append(ALFABETO_CODIGO.charAt(random.nextInt(ALFABETO_CODIGO.length())));
            }
            String codigo = sb.toString();
            if (!consultaDAO.existePorCodigoChegada(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("Nao foi possivel gerar um codigo de chegada unico.");
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

    private ConsultaTotem obter(Long id) {
        return consultaDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Consulta " + id + " nao encontrada."));
    }
}
