package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Usuario do painel (equipes, gestor e cooperativas). Tabela T_USUARIO_PAINEL.
 * A pessoa em situacao de rua nunca e usuaria: o totem nao tem login.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioPainel {

    @EqualsAndHashCode.Include
    private final Long id;
    private Long organizacaoId;
    private String nome;
    private String email;
    private PerfilAcesso perfilAcesso;
    private boolean ativo;

    private UsuarioPainel(Long id, Long organizacaoId, String nome, String email, PerfilAcesso perfilAcesso,
                          boolean ativo) {
        this.id = id;
        definir(organizacaoId, nome, email, perfilAcesso, ativo);
    }

    public static UsuarioPainel novo(Long organizacaoId, String nome, String email, PerfilAcesso perfilAcesso,
                                     boolean ativo) {
        return new UsuarioPainel(null, organizacaoId, nome, email, perfilAcesso, ativo);
    }

    public static UsuarioPainel reconstituir(Long id, Long organizacaoId, String nome, String email,
                                             PerfilAcesso perfilAcesso, boolean ativo) {
        return new UsuarioPainel(Objects.requireNonNull(id), organizacaoId, nome, email, perfilAcesso, ativo);
    }

    public void atualizar(Long organizacaoId, String nome, String email, PerfilAcesso perfilAcesso, boolean ativo) {
        definir(organizacaoId, nome, email, perfilAcesso, ativo);
    }

    /**
     * Quem pode atualizar a disponibilidade de um servico pelo painel:
     * usuario ativo; GESTOR atualiza qualquer servico; EQUIPE apenas os da propria organizacao;
     * COOPERATIVA nao atualiza servicos (cuida apenas da reciclagem).
     */
    public void garantirPodeAtualizar(Servico servico) {
        if (!ativo) {
            throw new RegraNegocioVioladaException("O usuario " + email + " esta inativo.");
        }
        switch (perfilAcesso) {
            case GESTOR -> { }
            case EQUIPE -> {
                if (!Objects.equals(organizacaoId, servico.getOrganizacaoId())) {
                    throw new RegraNegocioVioladaException(
                            "Usuarios de EQUIPE so atualizam servicos da propria organizacao.");
                }
            }
            case COOPERATIVA -> throw new RegraNegocioVioladaException(
                    "Usuarios de COOPERATIVA nao atualizam a disponibilidade de servicos.");
        }
    }

    /** Trocas de creditos sao registradas pela equipe ou pelo gestor (usuario ativo), nunca pela cooperativa. */
    public void garantirPodeRegistrarTroca() {
        if (!ativo) {
            throw new RegraNegocioVioladaException("O usuario " + email + " esta inativo.");
        }
        if (perfilAcesso == PerfilAcesso.COOPERATIVA) {
            throw new RegraNegocioVioladaException("Usuarios de COOPERATIVA nao registram trocas de creditos.");
        }
    }

    private void definir(Long organizacaoId, String nome, String email, PerfilAcesso perfilAcesso, boolean ativo) {
        this.organizacaoId = Validacoes.obrigatorio(organizacaoId, "A organizacao do usuario e obrigatoria.");
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 100);
        this.email = Validacoes.obrigatorio(Validacoes.emailOpcional(email, "email"), "O campo email e obrigatorio.");
        this.perfilAcesso = Validacoes.obrigatorio(perfilAcesso, "O perfil de acesso e obrigatorio (GESTOR, EQUIPE ou COOPERATIVA).");
        this.ativo = ativo;
    }
}
