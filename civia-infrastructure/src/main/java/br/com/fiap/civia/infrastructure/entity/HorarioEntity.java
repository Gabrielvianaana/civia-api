package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.infrastructure.converter.HoraConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/** Mapeamento JPA de T_HORARIO. */
@Entity
@Table(name = "T_HORARIO")
@Getter
@Setter
@NoArgsConstructor
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqHorario")
    @SequenceGenerator(name = "sqHorario", sequenceName = "SQ_HORARIO", allocationSize = 1)
    @Column(name = "id_horario")
    private Long id;

    @Column(name = "id_servico", nullable = false)
    private Long servicoId;

    @Column(name = "nr_dia_semana", nullable = false)
    private Integer diaSemana;

    @Convert(converter = HoraConverter.class)
    @Column(name = "hr_abertura", nullable = false, length = 5)
    private LocalTime abertura;

    @Convert(converter = HoraConverter.class)
    @Column(name = "hr_fechamento", nullable = false, length = 5)
    private LocalTime fechamento;
}
