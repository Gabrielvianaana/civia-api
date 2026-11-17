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

/** Mapeamento JPA de T_COLETA. */
@Entity
@Table(name = "T_COLETA")
@Getter
@Setter
@NoArgsConstructor
public class ColetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqColeta")
    @SequenceGenerator(name = "sqColeta", sequenceName = "SQ_COLETA", allocationSize = 1)
    @Column(name = "id_coleta")
    private Long id;

    @Column(name = "id_posto", nullable = false)
    private Long postoId;

    @Column(name = "id_cooperativa", nullable = false)
    private Long cooperativaId;

    @Column(name = "nr_peso_total_kg", nullable = false, precision = 7, scale = 2)
    private BigDecimal pesoTotalKg;

    @Column(name = "dt_coleta", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataColeta;
}
