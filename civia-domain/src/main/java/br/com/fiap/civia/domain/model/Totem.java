package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Entidade de dominio Totem: ponto fisico de consulta instalado em locais de grande circulacao.
 * Tabela: T_CIVIA_TOTEM.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Totem {

    public static final int TAMANHO_MAXIMO_IDENTIFICACAO = 30;
    public static final int TAMANHO_MAXIMO_LOCAL = 150;

    @EqualsAndHashCode.Include
    private final Long id;
    private String identificacao;
    private String local;
    private Coordenada coordenada;
    private boolean ativo;

    private Totem(Long id, String identificacao, String local, Coordenada coordenada, boolean ativo) {
        this.id = id;
        definirDados(identificacao, local, coordenada, ativo);
    }

    public static Totem novo(String identificacao, String local, Coordenada coordenada, boolean ativo) {
        return new Totem(null, identificacao, local, coordenada, ativo);
    }

    public static Totem reconstituir(Long id, String identificacao, String local, Coordenada coordenada, boolean ativo) {
        return new Totem(Objects.requireNonNull(id, "id e obrigatorio"), identificacao, local, coordenada, ativo);
    }

    public void atualizar(String identificacao, String local, Coordenada coordenada, boolean ativo) {
        definirDados(identificacao, local, coordenada, ativo);
    }

    /** Um totem desativado (manutencao, vandalismo) nao pode responder consultas. */
    public void garantirAtivo() {
        if (!ativo) {
            throw new RegraNegocioVioladaException("O totem " + identificacao + " esta inativo e nao pode realizar consultas.");
        }
    }

    private void definirDados(String identificacao, String local, Coordenada coordenada, boolean ativo) {
        this.identificacao = textoObrigatorio(identificacao, "identificacao", TAMANHO_MAXIMO_IDENTIFICACAO);
        this.local = textoObrigatorio(local, "local", TAMANHO_MAXIMO_LOCAL);
        if (coordenada == null) {
            throw new DadoInvalidoException("A localizacao (latitude/longitude) do totem e obrigatoria.");
        }
        this.coordenada = coordenada;
        this.ativo = ativo;
    }

    private static String textoObrigatorio(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("O campo " + campo + " e obrigatorio.");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new DadoInvalidoException("O campo " + campo + " deve ter no maximo " + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }
}
