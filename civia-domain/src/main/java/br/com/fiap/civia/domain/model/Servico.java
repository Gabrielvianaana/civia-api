package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Servico parceiro exibido nos totens (RF10). Tabelas T_SERVICO e T_SERVICO_PERFIL.
 * A organizacao responsavel e referenciada pelo id (outro agregado).
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servico {

    public static final int CAPACIDADE_MAXIMA = 99_999;

    @EqualsAndHashCode.Include
    private final Long id;
    private Long organizacaoId;
    private TipoServico tipo;
    private String nome;
    private Endereco endereco;
    private Coordenada coordenada;
    private Integer capacidade;
    private boolean funciona24h;
    private StatusAprovacao statusAprovacao;
    private final LocalDateTime dataCadastro;
    private Set<PerfilAtendido> perfis;

    private Servico(Long id, Long organizacaoId, TipoServico tipo, String nome, Endereco endereco,
                    Coordenada coordenada, Integer capacidade, boolean funciona24h, StatusAprovacao statusAprovacao,
                    LocalDateTime dataCadastro, Set<PerfilAtendido> perfis) {
        this.id = id;
        this.dataCadastro = Objects.requireNonNull(dataCadastro, "dataCadastro e obrigatoria");
        definir(organizacaoId, tipo, nome, endereco, coordenada, capacidade, funciona24h, statusAprovacao, perfis);
    }

    /** Novo cadastro: sem status informado, nasce PENDENTE de aprovacao do gestor (RN06 / US13). */
    public static Servico novo(Long organizacaoId, TipoServico tipo, String nome, Endereco endereco,
                               Coordenada coordenada, Integer capacidade, boolean funciona24h,
                               StatusAprovacao statusAprovacao, LocalDateTime dataCadastro, Set<PerfilAtendido> perfis) {
        return new Servico(null, organizacaoId, tipo, nome, endereco, coordenada, capacidade, funciona24h,
                statusAprovacao == null ? StatusAprovacao.PENDENTE : statusAprovacao, dataCadastro, perfis);
    }

    public static Servico reconstituir(Long id, Long organizacaoId, TipoServico tipo, String nome, Endereco endereco,
                                       Coordenada coordenada, Integer capacidade, boolean funciona24h,
                                       StatusAprovacao statusAprovacao, LocalDateTime dataCadastro,
                                       Set<PerfilAtendido> perfis) {
        return new Servico(Objects.requireNonNull(id), organizacaoId, tipo, nome, endereco, coordenada, capacidade,
                funciona24h, statusAprovacao, dataCadastro, perfis);
    }

    public void atualizar(Long organizacaoId, TipoServico tipo, String nome, Endereco endereco, Coordenada coordenada,
                          Integer capacidade, boolean funciona24h, StatusAprovacao statusAprovacao,
                          Set<PerfilAtendido> perfis) {
        definir(organizacaoId, tipo, nome, endereco, coordenada, capacidade, funciona24h,
                statusAprovacao == null ? this.statusAprovacao : statusAprovacao, perfis);
    }

    public boolean ehAbrigo() {
        return tipo.ehAbrigo();
    }

    public boolean estaAprovado() {
        return statusAprovacao == StatusAprovacao.APROVADO;
    }

    public boolean atendePerfil(PerfilAtendido perfil) {
        return perfil != null && perfis.stream().anyMatch(p -> Objects.equals(p.getId(), perfil.getId()));
    }

    public Set<PerfilAtendido> getPerfis() {
        return Collections.unmodifiableSet(perfis);
    }

    private void definir(Long organizacaoId, TipoServico tipo, String nome, Endereco endereco, Coordenada coordenada,
                         Integer capacidade, boolean funciona24h, StatusAprovacao statusAprovacao,
                         Set<PerfilAtendido> perfis) {
        this.organizacaoId = Validacoes.obrigatorio(organizacaoId, "A organizacao responsavel e obrigatoria.");
        this.tipo = Validacoes.obrigatorio(tipo, "O tipo de servico e obrigatorio.");
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 120);
        this.endereco = Validacoes.obrigatorio(endereco, "O endereco e obrigatorio.");
        this.coordenada = Validacoes.obrigatorio(coordenada, "A localizacao (latitude/longitude) e obrigatoria.");
        if (capacidade != null && (capacidade <= 0 || capacidade > CAPACIDADE_MAXIMA)) {
            throw new DadoInvalidoException("A capacidade deve ser maior que zero (e no maximo " + CAPACIDADE_MAXIMA + ").");
        }
        this.capacidade = capacidade;
        this.funciona24h = funciona24h;
        this.statusAprovacao = Validacoes.obrigatorio(statusAprovacao, "O status de aprovacao e obrigatorio.");
        this.perfis = perfis == null ? new LinkedHashSet<>() : new LinkedHashSet<>(perfis);
        if (ehAbrigo() && this.perfis.isEmpty()) {
            throw new RegraNegocioVioladaException("Um abrigo deve informar ao menos um perfil atendido.");
        }
    }
}
