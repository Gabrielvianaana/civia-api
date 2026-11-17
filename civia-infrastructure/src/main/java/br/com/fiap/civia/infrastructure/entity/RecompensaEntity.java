package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.infrastructure.converter.SimNaoConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mapeamento JPA de T_RECOMPENSA. */
@Entity
@Table(name = "T_RECOMPENSA")
@Getter
@Setter
@NoArgsConstructor
public class RecompensaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqRecompensa")
    @SequenceGenerator(name = "sqRecompensa", sequenceName = "SQ_RECOMPENSA", allocationSize = 1)
    @Column(name = "id_recompensa")
    private Long id;

    @Column(name = "id_organizacao_parceira", nullable = false)
    private Long organizacaoParceiraId;

    @Column(name = "nm_recompensa", nullable = false, length = 80)
    private String nome;

    @Column(name = "nr_custo_creditos", nullable = false)
    private Integer custoCreditos;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_ativo", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean ativo;
}
