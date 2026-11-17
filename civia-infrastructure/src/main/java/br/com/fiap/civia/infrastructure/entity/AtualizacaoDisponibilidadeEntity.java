package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.OrigemAtualizacao;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Mapeamento JPA de T_ATUALIZACAO. */
@Entity
@Table(name = "T_ATUALIZACAO")
@Getter
@Setter
@NoArgsConstructor
public class AtualizacaoDisponibilidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqAtualizacao")
    @SequenceGenerator(name = "sqAtualizacao", sequenceName = "SQ_ATUALIZACAO", allocationSize = 1)
    @Column(name = "id_atualizacao")
    private Long id;

    @Column(name = "id_servico", nullable = false)
    private Long servicoId;

    @Column(name = "id_usuario")
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_disponibilidade", nullable = false, length = 15)
    private StatusDisponibilidade status;

    @Column(name = "qt_vagas")
    private Integer vagas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_origem", nullable = false, length = 15)
    private OrigemAtualizacao origem;

    @Column(name = "dt_atualizacao", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataHora;
}
