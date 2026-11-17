package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.UsuarioPainelRequest;
import br.com.fiap.civia.application.dto.UsuarioPainelResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.AtualizacaoDisponibilidadeDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.PerfilAcesso;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO dos usuarios do painel (T_USUARIO_PAINEL). E-mail unico. */
@Service
@RequiredArgsConstructor
public class UsuarioPainelBO {

    private final UsuarioPainelDAO usuarioDAO;
    private final OrganizacaoDAO organizacaoDAO;
    private final AtualizacaoDisponibilidadeDAO atualizacaoDAO;

    @Transactional(readOnly = true)
    public List<UsuarioPainelResponse> listar(Long organizacaoId) {
        return usuarioDAO.listar(organizacaoId).stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioPainelResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public UsuarioPainelResponse cadastrar(UsuarioPainelRequest r) {
        garantirOrganizacao(r.organizacaoId());
        UsuarioPainel u = executar(() -> UsuarioPainel.novo(r.organizacaoId(), r.nome(), r.email(), perfil(r.perfilAcesso()),
                r.ativo() == null || r.ativo()));
        garantirEmailUnico(u.getEmail(), null);
        return DtoMapper.paraResponse(usuarioDAO.salvar(u));
    }

    @Transactional
    public UsuarioPainelResponse atualizar(Long id, UsuarioPainelRequest r) {
        UsuarioPainel u = obter(id);
        garantirOrganizacao(r.organizacaoId());
        executar(() -> u.atualizar(r.organizacaoId(), r.nome(), r.email(), perfil(r.perfilAcesso()),
                r.ativo() == null || r.ativo()));
        garantirEmailUnico(u.getEmail(), id);
        return DtoMapper.paraResponse(usuarioDAO.salvar(u));
    }

    /** Usuario com atualizacoes registradas deve ser desativado (ativo=false), nao excluido: preserva o historico. */
    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (atualizacaoDAO.existePorUsuario(id)) {
            throw new ConflitoException("O usuario " + id + " possui atualizacoes registradas. Desative-o (ativo=false) em vez de excluir.");
        }
        usuarioDAO.excluir(id);
    }

    UsuarioPainel obter(Long id) {
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado."));
    }

    private void garantirOrganizacao(Long organizacaoId) {
        if (!organizacaoDAO.existePorId(organizacaoId)) {
            throw new RecursoNaoEncontradoException("Organizacao " + organizacaoId + " nao encontrada.");
        }
    }

    private void garantirEmailUnico(String email, Long idIgnorado) {
        if (usuarioDAO.existePorEmail(email, idIgnorado)) {
            throw new ConflitoException("Ja existe um usuario com o e-mail " + email + ".");
        }
    }

    private static PerfilAcesso perfil(String perfil) {
        return Conversor.paraEnum(PerfilAcesso.class, perfil, "perfilAcesso");
    }
}
