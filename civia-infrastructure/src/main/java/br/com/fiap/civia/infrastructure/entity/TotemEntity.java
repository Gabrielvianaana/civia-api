package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.StatusTotem;
import br.com.fiap.civia.infrastructure.converter.SimNaoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Mapeamento JPA de T_TOTEM. */
@Entity
@Table(name = "T_TOTEM")
@Getter
@Setter
@NoArgsConstructor
public class TotemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqTotem")
    @SequenceGenerator(name = "sqTotem", sequenceName = "SQ_TOTEM", allocationSize = 1)
    @Column(name = "id_totem")
    private Long id;

    @Column(name = "cd_totem", nullable = false, length = 12)
    private String codigo;

    @Column(name = "ds_logradouro", nullable = false, length = 120)
    private String logradouro;

    @Column(name = "nm_bairro", nullable = false, length = 60)
    private String bairro;

    @Column(name = "nr_latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "nr_longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_totem", nullable = false, length = 12)
    private StatusTotem status;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_papel_impressora", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean papelImpressora;

    @Column(name = "dt_instalacao", nullable = false)
    private LocalDate dataInstalacao;

    @Column(name = "dt_ultima_sincronizacao", columnDefinition = "DATE")
    private LocalDateTime ultimaSincronizacao;
}
