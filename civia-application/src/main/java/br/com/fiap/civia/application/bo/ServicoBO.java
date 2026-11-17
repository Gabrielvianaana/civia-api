package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ServicoRequest;
import br.com.fiap.civia.application.dto.ServicoResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.dao.HorarioDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO do cadastro de servicos parceiros (RF10 / US13) - T_SERVICO e T_SERVICO_PERFIL. */
@Service
@RequiredArgsConstructor
public class ServicoBO {

    private final ServicoDAO servicoDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final TipoServicoDAO tipoDAO;
    private final PerfilAtendidoDAO perfilDAO;
    private final HorarioDAO horarioDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final ConsultaTotemDAO consultaDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar(Integer tipoServicoId, String statusAprovacao, Long organizacaoId) {
        StatusAprovacao status = Conversor.paraEnum(StatusAprovacao.class, statusAprovacao, "statusAprovacao");
        return servicoDAO.listar(tipoServicoId, status, organizacaoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public ServicoResponse cadastrar(ServicoRequest r) {
        Referencias ref = resolverReferencias(r);
        Servico s = executar(() -> Servico.novo(r.organizacaoId(), ref.tipo(), r.nome(),
                new Endereco(r.logradouro(), r.numero(), r.bairro()), Coordenada.de(r.latitude(), r.longitude()),
                r.capacidade(), Boolean.TRUE.equals(r.funciona24h()), ref.status(),
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS), ref.perfis()));
        return DtoMapper.paraResponse(servicoDAO.salvar(s));
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest r) {
        Servico s = obter(id);
        Referencias ref = resolverReferencias(r);
        executar(() -> s.atualizar(r.organizacaoId(), ref.tipo(), r.nome(),
                new Endereco(r.logradouro(), r.numero(), r.bairro()), Coordenada.de(r.latitude(), r.longitude()),
                r.capacidade(), Boolean.TRUE.equals(r.funciona24h()), ref.status(), ref.perfis()));
        return DtoMapper.paraResponse(servicoDAO.salvar(s));
    }

    /**
     * Exclui o servico com seus horarios, perfis e historico de atualizacoes.
     * Se ja foi escolhido em consultas de totem, nao exclui (preserva indicadores): use REPROVADO.
     */
    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (consultaDAO.existePorServico(id)) {
            throw new ConflitoException("O servico " + id + " ja foi escolhido em consultas de totem. Reprove-o "
                    + "(statusAprovacao=REPROVADO) para tira-lo dos totens sem perder o historico.");
        }
        horarioDAO.excluirPorServico(id);
        atualizacaoDAO.excluirPorServico(id);
        servicoDAO.excluir(id);
    }

    Servico obter(Long id) {
        return servicoDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Servico " + id + " nao encontrado."));
    }

    private Referencias resolverReferencias(ServicoRequest r) {
        if (!organizacaoDAO.existePorId(r.organizacaoId())) {
            throw new RecursoNaoEncontradoException("Organizacao " + r.organizacaoId() + " nao encontrada.");
        }
        TipoServico tipo = tipoDAO.buscarPorId(r.tipoServicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de servico " + r.tipoServicoId() + " nao encontrado."));
        Set<Integer> ids = r.perfilIds() == null ? Set.of() : new HashSet<>(r.perfilIds());
        Set<PerfilAtendido> perfis = new LinkedHashSet<>(ids.isEmpty() ? List.of() : perfilDAO.buscarPorIds(ids));
        if (perfis.size() != ids.size()) {
            Set<Integer> encontrados = perfis.stream().map(PerfilAtendido::getId).collect(Collectors.toSet());
            ids.removeAll(encontrados);
            throw new RecursoNaoEncontradoException("Perfil(is) nao encontrado(s): " + ids + ".");
        }
        StatusAprovacao status = Conversor.paraEnum(StatusAprovacao.class, r.statusAprovacao(), "statusAprovacao");
        return new Referencias(tipo, perfis, status);
    }

    private record Referencias(TipoServico tipo, Set<PerfilAtendido> perfis, StatusAprovacao status) {
    }
}
