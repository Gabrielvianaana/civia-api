package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/** Totem de consulta instalado em ponto de grande circulacao. Tabela T_TOTEM. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Totem {

    @EqualsAndHashCode.Include
    private final Long id;
    private String codigo;
    private Endereco endereco;
    private Coordenada coordenada;
    private StatusTotem status;
    private boolean papelImpressora;
    private LocalDate dataInstalacao;
    private LocalDateTime ultimaSincronizacao;

    private Totem(Long id, String codigo, Endereco endereco, Coordenada coordenada, StatusTotem status,
                  boolean papelImpressora, LocalDate dataInstalacao, LocalDateTime ultimaSincronizacao) {
        this.id = id;
        definir(codigo, endereco, coordenada, status, papelImpressora, dataInstalacao, ultimaSincronizacao);
    }

    public static Totem novo(String codigo, Endereco endereco, Coordenada coordenada, StatusTotem status,
                             boolean papelImpressora, LocalDate dataInstalacao, LocalDateTime ultimaSincronizacao) {
        return new Totem(null, codigo, endereco, coordenada, status == null ? StatusTotem.ONLINE : status,
                papelImpressora, dataInstalacao, ultimaSincronizacao);
    }

    public static Totem reconstituir(Long id, String codigo, Endereco endereco, Coordenada coordenada,
                                     StatusTotem status, boolean papelImpressora, LocalDate dataInstalacao,
                                     LocalDateTime ultimaSincronizacao) {
        return new Totem(Objects.requireNonNull(id), codigo, endereco, coordenada, status, papelImpressora,
                dataInstalacao, ultimaSincronizacao);
    }

    public void atualizar(String codigo, Endereco endereco, Coordenada coordenada, StatusTotem status,
                          boolean papelImpressora, LocalDate dataInstalacao, LocalDateTime ultimaSincronizacao) {
        definir(codigo, endereco, coordenada, status == null ? this.status : status, papelImpressora, dataInstalacao,
                ultimaSincronizacao);
    }

    /** Totem em manutencao nao responde consultas. */
    public void garantirDisponivelParaConsulta() {
        if (status == StatusTotem.MANUTENCAO) {
            throw new RegraNegocioVioladaException("O totem " + codigo + " esta em manutencao e nao pode realizar consultas.");
        }
    }

    private void definir(String codigo, Endereco endereco, Coordenada coordenada, StatusTotem status,
                         boolean papelImpressora, LocalDate dataInstalacao, LocalDateTime ultimaSincronizacao) {
        this.codigo = Validacoes.textoObrigatorio(codigo, "codigo", 12).toUpperCase();
        this.endereco = Validacoes.obrigatorio(endereco, "O endereco do totem e obrigatorio.");
        this.coordenada = Validacoes.obrigatorio(coordenada, "A localizacao (latitude/longitude) do totem e obrigatoria.");
        this.status = Validacoes.obrigatorio(status, "O status do totem e obrigatorio.");
        this.papelImpressora = papelImpressora;
        this.dataInstalacao = Validacoes.obrigatorio(dataInstalacao, "A data de instalacao e obrigatoria.");
        this.ultimaSincronizacao = ultimaSincronizacao;
    }
}
