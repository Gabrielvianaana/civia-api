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

/** Mapeamento JPA de T_MATERIAL. */
@Entity
@Table(name = "T_MATERIAL")
@Getter
@Setter
@NoArgsConstructor
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqMaterial")
    @SequenceGenerator(name = "sqMaterial", sequenceName = "SQ_MATERIAL", allocationSize = 1)
    @Column(name = "id_material")
    private Integer id;

    @Column(name = "nm_material", nullable = false, length = 40)
    private String nome;

    @Column(name = "nr_creditos_por_kg", nullable = false)
    private Integer creditosPorKg;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_aceito", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean aceito;
}
