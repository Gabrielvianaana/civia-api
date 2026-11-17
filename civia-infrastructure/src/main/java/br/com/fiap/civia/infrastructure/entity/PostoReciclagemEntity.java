package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.StatusPosto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/** Mapeamento JPA de T_POSTO_RECICLAGEM. */
@Entity
@Table(name = "T_POSTO_RECICLAGEM")
@Getter
@Setter
@NoArgsConstructor
public class PostoReciclagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqPostoReciclagem")
    @SequenceGenerator(name = "sqPostoReciclagem", sequenceName = "SQ_POSTO_RECICLAGEM", allocationSize = 1)
    @Column(name = "id_posto")
    private Long id;

    @Column(name = "id_totem", nullable = false)
    private Long totemId;

    @Column(name = "id_cooperativa", nullable = false)
    private Long cooperativaId;

    @Column(name = "nr_capacidade_kg", nullable = false, precision = 6, scale = 2)
    private BigDecimal capacidadeKg;

    @Column(name = "nr_limite_diario_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal limiteDiarioKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_posto", nullable = false, length = 12)
    private StatusPosto status;
}
