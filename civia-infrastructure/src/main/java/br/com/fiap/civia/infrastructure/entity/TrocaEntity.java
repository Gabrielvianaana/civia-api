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

import java.time.LocalDateTime;

/** Mapeamento JPA de T_TROCA. */
@Entity
@Table(name = "T_TROCA")
@Getter
@Setter
@NoArgsConstructor
public class TrocaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqTroca")
    @SequenceGenerator(name = "sqTroca", sequenceName = "SQ_TROCA", allocationSize = 1)
    @Column(name = "id_troca")
    private Long id;

    @Column(name = "id_cartao", nullable = false)
    private Long cartaoId;

    @Column(name = "id_recompensa", nullable = false)
    private Long recompensaId;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId;

    @Column(name = "nr_creditos_usados", nullable = false)
    private Integer creditosUsados;

    @Column(name = "dt_troca", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataTroca;
}
