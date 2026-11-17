package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.HorarioFuncionamento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade de dominio Servico (abrigo, refeicao, banho ou atendimento) cadastrado por parceiros (RF10).
 * Classe Java pura, sem anotacoes de persistencia. Tabelas: T_CIVIA_SERVICO e T_CIVIA_SERVICO_PERFIL.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servico {

    public static final int TAMANHO_MAXIMO_NOME = 100;
    public static final int TAMANHO_MAXIMO_ENDERECO = 200;

    @EqualsAndHashCode.Include
    private final Long id;
    private String nome;
    private TipoServico tipo;
    private String endereco;
    private Coordenada coordenada;
    private HorarioFuncionamento horario;
    private Set<PerfilAtendido> perfis;
    private boolean aprovado;
    private final LocalDateTime dataCadastro;

    private Servico(Long id, String nome, TipoServico tipo, String endereco, Coordenada coordenada,
                    HorarioFuncionamento horario, Set<PerfilAtendido> perfis, boolean aprovado,
                    LocalDateTime dataCadastro) {
        this.id = id;
        this.dataCadastro = Objects.requireNonNull(dataCadastro, "dataCadastro e obrigatoria");
        definirDados(nome, tipo, endereco, coordenada, horario, perfis, aprovado);
    }

    /** Fabrica para um novo cadastro (ainda sem id; o banco gera a identidade). */
    public static Servico novo(String nome, TipoServico tipo, String endereco, Coordenada coordenada,
                               HorarioFuncionamento horario, Set<PerfilAtendido> perfis, boolean aprovado,
                               LocalDateTime dataCadastro) {
        return new Servico(null, nome, tipo, endereco, coordenada, horario, perfis, aprovado, dataCadastro);
    }

    /** Fabrica para reconstituir um servico ja persistido (usada pelos DAOs). */
    public static Servico reconstituir(Long id, String nome, TipoServico tipo, String endereco, Coordenada coordenada,
                                       HorarioFuncionamento horario, Set<PerfilAtendido> perfis, boolean aprovado,
                                       LocalDateTime dataCadastro) {
        return new Servico(Objects.requireNonNull(id, "id e obrigatorio"), nome, tipo, endereco, coordenada,
                horario, perfis, aprovado, dataCadastro);
    }

    public void atualizar(String nome, TipoServico tipo, String endereco, Coordenada coordenada,
                          HorarioFuncionamento horario, Set<PerfilAtendido> perfis, boolean aprovado) {
        definirDados(nome, tipo, endereco, coordenada, horario, perfis, aprovado);
    }

    public boolean ehAbrigo() {
        return tipo == TipoServico.ABRIGO;
    }

    public boolean atendePerfil(PerfilAtendido perfil) {
        return perfis.contains(perfil);
    }

    public Set<PerfilAtendido> getPerfis() {
        return Collections.unmodifiableSet(perfis);
    }

    private void definirDados(String nome, TipoServico tipo, String endereco, Coordenada coordenada,
                              HorarioFuncionamento horario, Set<PerfilAtendido> perfis, boolean aprovado) {
        this.nome = textoObrigatorio(nome, "nome", TAMANHO_MAXIMO_NOME);
        this.tipo = exigir(tipo, "O tipo do servico e obrigatorio (ABRIGO, REFEICAO, BANHO ou ATENDIMENTO).");
        this.endereco = textoObrigatorio(endereco, "endereco", TAMANHO_MAXIMO_ENDERECO);
        this.coordenada = exigir(coordenada, "A localizacao (latitude/longitude) e obrigatoria.");
        this.horario = exigir(horario, "O horario de funcionamento e obrigatorio.");
        this.perfis = (perfis == null || perfis.isEmpty()) ? EnumSet.noneOf(PerfilAtendido.class) : EnumSet.copyOf(perfis);
        this.aprovado = aprovado;
        if (ehAbrigo() && this.perfis.isEmpty()) {
            throw new RegraNegocioVioladaException("Um abrigo deve informar ao menos um perfil atendido.");
        }
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

    private static <T> T exigir(T valor, String mensagem) {
        if (valor == null) {
            throw new DadoInvalidoException(mensagem);
        }
        return valor;
    }
}
