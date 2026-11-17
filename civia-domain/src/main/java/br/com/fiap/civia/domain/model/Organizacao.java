package br.com.fiap.civia.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Organizacao da rede (Centro POP, ONG, cooperativa, prefeitura ou parceiro). Tabela T_ORGANIZACAO.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organizacao {

    @EqualsAndHashCode.Include
    private final Long id;
    private String nome;
    private TipoOrganizacao tipo;
    private String telefone;
    private String email;

    private Organizacao(Long id, String nome, TipoOrganizacao tipo, String telefone, String email) {
        this.id = id;
        definir(nome, tipo, telefone, email);
    }

    public static Organizacao nova(String nome, TipoOrganizacao tipo, String telefone, String email) {
        return new Organizacao(null, nome, tipo, telefone, email);
    }

    public static Organizacao reconstituir(Long id, String nome, TipoOrganizacao tipo, String telefone, String email) {
        return new Organizacao(Objects.requireNonNull(id), nome, tipo, telefone, email);
    }

    public void atualizar(String nome, TipoOrganizacao tipo, String telefone, String email) {
        definir(nome, tipo, telefone, email);
    }

    private void definir(String nome, TipoOrganizacao tipo, String telefone, String email) {
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 120);
        this.tipo = Validacoes.obrigatorio(tipo, "O tipo da organizacao e obrigatorio.");
        this.telefone = Validacoes.textoOpcional(telefone, "telefone", 15);
        this.email = Validacoes.emailOpcional(email, "email");
    }
}
