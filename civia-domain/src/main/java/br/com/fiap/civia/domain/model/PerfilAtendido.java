package br.com.fiap.civia.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/** Perfil de publico atendido (homem, mulher, familia...). Tabela T_PERFIL_ATENDIDO. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PerfilAtendido {

    @EqualsAndHashCode.Include
    private final Integer id;
    private String descricao;

    private PerfilAtendido(Integer id, String descricao) {
        this.id = id;
        definir(descricao);
    }

    public static PerfilAtendido novo(String descricao) {
        return new PerfilAtendido(null, descricao);
    }

    public static PerfilAtendido reconstituir(Integer id, String descricao) {
        return new PerfilAtendido(Objects.requireNonNull(id), descricao);
    }

    public void atualizar(String descricao) {
        definir(descricao);
    }

    public boolean temDescricao(String outra) {
        return outra != null && TipoServico.normalizar(descricao).equals(TipoServico.normalizar(outra));
    }

    private void definir(String descricao) {
        this.descricao = Validacoes.textoObrigatorio(descricao, "descricao", 40);
    }
}
