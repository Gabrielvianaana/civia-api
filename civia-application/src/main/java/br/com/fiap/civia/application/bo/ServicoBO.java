package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.ServicoRequest;
import br.com.fiap.civia.application.dto.ServicoResponse;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.ServicoDtoMapper;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.ServicoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/**
 * BO (Business Object) do cadastro de servicos parceiros (RF10 / US13).
 */
@Service
@RequiredArgsConstructor
public class ServicoBO {

    private final ServicoDAO servicoDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar(String tipo, Boolean aprovado) {
        TipoServico tipoFiltro = Conversor.paraEnum(TipoServico.class, tipo, "tipo");
        return servicoDAO.listar(tipoFiltro, aprovado).stream().map(ServicoDtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscar(Long id) {
        return ServicoDtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public ServicoResponse cadastrar(ServicoRequest request) {
        Servico servico = executar(() -> Servico.novo(
                request.nome(),
                tipoObrigatorio(request.tipo()),
                request.endereco(),
                Coordenada.de(request.latitude(), request.longitude()),
                horario(request),
                perfis(request.perfis()),
                Boolean.TRUE.equals(request.aprovado()),
                LocalDateTime.now(clock)));
        return ServicoDtoMapper.paraResponse(servicoDAO.salvar(servico));
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest request) {
        Servico servico = obter(id);
        executar(() -> servico.atualizar(
                request.nome(),
                tipoObrigatorio(request.tipo()),
                request.endereco(),
                Coordenada.de(request.latitude(), request.longitude()),
                horario(request),
                perfis(request.perfis()),
                Boolean.TRUE.equals(request.aprovado())));
        return ServicoDtoMapper.paraResponse(servicoDAO.salvar(servico));
    }

    /** Exclui o servico e o historico de atualizacoes dele. */
    @Transactional
    public void excluir(Long id) {
        if (!servicoDAO.existePorId(id)) {
            throw naoEncontrado(id);
        }
        atualizacaoDAO.excluirPorServico(id);
        servicoDAO.excluir(id);
    }

    private Servico obter(Long id) {
        return servicoDAO.buscarPorId(id).orElseThrow(() -> naoEncontrado(id));
    }

    private static RecursoNaoEncontradoException naoEncontrado(Long id) {
        return new RecursoNaoEncontradoException("Servico " + id + " nao encontrado.");
    }

    private static TipoServico tipoObrigatorio(String tipo) {
        return Conversor.paraEnum(TipoServico.class, tipo, "tipo");
    }

    private static HorarioFuncionamento horario(ServicoRequest request) {
        return new HorarioFuncionamento(
                Conversor.paraHora(request.horaAbertura(), "horaAbertura"),
                Conversor.paraHora(request.horaFechamento(), "horaFechamento"));
    }

    private static Set<PerfilAtendido> perfis(Set<String> perfis) {
        Set<PerfilAtendido> convertidos = EnumSet.noneOf(PerfilAtendido.class);
        if (perfis != null) {
            perfis.forEach(p -> {
                PerfilAtendido perfil = Conversor.paraEnum(PerfilAtendido.class, p, "perfis");
                if (perfil != null) {
                    convertidos.add(perfil);
                }
            });
        }
        return convertidos;
    }
}
