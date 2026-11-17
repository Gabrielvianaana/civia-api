package br.com.fiap.civia.infrastructure.entity;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Mapeamento JPA de T_ENTREGA. */
@Entity
@Table(name = "T_ENTREGA")
@Getter
@Setter
@NoArgsConstructor
public class EntregaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqEntrega")
    @SequenceGenerator(name = "sqEntrega", sequenceName = "SQ_ENTREGA", allocationSize = 1)
    @Column(name = "id_entrega")
    private Long id;

    @Column(name = "id_cartao", nullable = false)
    private Long cartaoId;

    @Column(name = "id_posto", nullable = false)
    private Long postoId;

    @Column(name = "id_material", nullable = false)
    private Integer materialId;

    @Column(name = "nr_peso_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "nr_creditos_gerados", nullable = false)
    private Integer creditosGerados;

    @Column(name = "dt_entrega", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataEntrega;
}
