package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.infrastructure.converter.SimNaoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapeamento JPA de T_SERVICO e da associativa N:N T_SERVICO_PERFIL.
 * A organizacao e referenciada pelo id (outro agregado); tipo e perfis sao dados de referencia.
 */
@Entity
@Table(name = "T_SERVICO")
@Getter
@Setter
@NoArgsConstructor
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqServico")
    @SequenceGenerator(name = "sqServico", sequenceName = "SQ_SERVICO", allocationSize = 1)
    @Column(name = "id_servico")
    private Long id;

    @Column(name = "id_organizacao", nullable = false)
    private Long organizacaoId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_tipo_servico", nullable = false)
    private TipoServicoEntity tipo;

    @Column(name = "nm_servico", nullable = false, length = 120)
    private String nome;

    @Column(name = "ds_logradouro", nullable = false, length = 120)
    private String logradouro;

    @Column(name = "nr_endereco", length = 10)
    private String numero;

    @Column(name = "nm_bairro", nullable = false, length = 60)
    private String bairro;

    @Column(name = "nr_latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "nr_longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "nr_capacidade")
    private Integer capacidade;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_24h", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean funciona24h;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_aprovacao", nullable = false, length = 10)
    private StatusAprovacao statusAprovacao;

    @Column(name = "dt_cadastro", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataCadastro;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_SERVICO_PERFIL",
            joinColumns = @JoinColumn(name = "id_servico"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<PerfilAtendidoEntity> perfis = new HashSet<>();
}
