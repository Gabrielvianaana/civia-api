package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Material reciclavel. So lata de aluminio e garrafa PET sao aceitas (RN09); os demais ficam cadastrados
 * como nao aceitos para o posto recusar com uma mensagem clara. Tabela T_MATERIAL.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Material {

    @EqualsAndHashCode.Include
    private final Integer id;
    private String nome;
    private int creditosPorKg;
    private boolean aceito;

    private Material(Integer id, String nome, Integer creditosPorKg, boolean aceito) {
        this.id = id;
        definir(nome, creditosPorKg, aceito);
    }

    public static Material novo(String nome, Integer creditosPorKg, boolean aceito) {
        return new Material(null, nome, creditosPorKg, aceito);
    }

    public static Material reconstituir(Integer id, String nome, Integer creditosPorKg, boolean aceito) {
        return new Material(Objects.requireNonNull(id), nome, creditosPorKg, aceito);
    }

    public void atualizar(String nome, Integer creditosPorKg, boolean aceito) {
        definir(nome, creditosPorKg, aceito);
    }

    /** RN09: material nao aceito e recusado no posto. */
    public void garantirAceito() {
        if (!aceito) {
            throw new RegraNegocioVioladaException("O material " + nome
                    + " nao e aceito no posto (RN09: somente latas de aluminio e garrafas PET).");
        }
    }

    /** Creditos gerados pelo peso entregue (arredondados para o inteiro mais proximo). */
    public int calcularCreditos(BigDecimal pesoKg) {
        return pesoKg.multiply(BigDecimal.valueOf(creditosPorKg)).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    private void definir(String nome, Integer creditosPorKg, boolean aceito) {
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 40);
        int creditos = Validacoes.obrigatorio(creditosPorKg, "O campo creditosPorKg e obrigatorio.");
        if (creditos < 0 || creditos > 99_999) {
            throw new DadoInvalidoException("O campo creditosPorKg deve estar entre 0 e 99999.");
        }
        // ck_mat_aceito: aceito gera creditos; nao aceito tem 0 creditos por kg
        if (aceito && creditos == 0) {
            throw new DadoInvalidoException("Material aceito deve gerar creditos por kg (maior que zero).");
        }
        if (!aceito && creditos != 0) {
            throw new DadoInvalidoException("Material nao aceito deve ter 0 creditos por kg.");
        }
        this.creditosPorKg = creditos;
        this.aceito = aceito;
    }
}
