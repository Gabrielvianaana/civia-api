package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/** Item do catalogo de recompensas doado por um parceiro; so itens extras (RN01 e RN11). Tabela T_RECOMPENSA. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recompensa {

    @EqualsAndHashCode.Include
    private final Long id;
    private Long organizacaoParceiraId;
    private String nome;
    private int custoCreditos;
    private boolean ativo;

    private Recompensa(Long id, Long organizacaoParceiraId, String nome, Integer custoCreditos, boolean ativo) {
        this.id = id;
        definir(organizacaoParceiraId, nome, custoCreditos, ativo);
    }

    public static Recompensa nova(Long organizacaoParceiraId, String nome, Integer custoCreditos, boolean ativo) {
        return new Recompensa(null, organizacaoParceiraId, nome, custoCreditos, ativo);
    }

    public static Recompensa reconstituir(Long id, Long organizacaoParceiraId, String nome, Integer custoCreditos,
                                          boolean ativo) {
        return new Recompensa(Objects.requireNonNull(id), organizacaoParceiraId, nome, custoCreditos, ativo);
    }

    public void atualizar(Long organizacaoParceiraId, String nome, Integer custoCreditos, boolean ativo) {
        definir(organizacaoParceiraId, nome, custoCreditos, ativo);
    }

    public void garantirDisponivel() {
        if (!ativo) {
            throw new RegraNegocioVioladaException("A recompensa " + nome + " nao esta disponivel no catalogo.");
        }
    }

    private void definir(Long organizacaoParceiraId, String nome, Integer custoCreditos, boolean ativo) {
        this.organizacaoParceiraId = Validacoes.obrigatorio(organizacaoParceiraId, "A organizacao parceira e obrigatoria.");
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 80);
        int custo = Validacoes.obrigatorio(custoCreditos, "O campo custoCreditos e obrigatorio.");
        if (custo <= 0 || custo > 999_999) {
            throw new DadoInvalidoException("O campo custoCreditos deve estar entre 1 e 999999.");
        }
        this.custoCreditos = custo;
        this.ativo = ativo;
    }
}
