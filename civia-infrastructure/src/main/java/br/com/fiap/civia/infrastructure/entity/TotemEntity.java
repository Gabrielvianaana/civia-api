package br.com.fiap.civia.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;

/** Mapeamento JPA da tabela T_CIVIA_TOTEM. */
@Entity
@Table(name = "T_CIVIA_TOTEM")
@Getter
@Setter
@NoArgsConstructor
public class TotemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TOTEM")
    private Long id;

    @Column(name = "IDENTIFICACAO", nullable = false, length = 30, unique = true)
    private String identificacao;

    @Column(name = "LOCAL_INSTALACAO", nullable = false, length = 150)
    private String local;

    @Column(name = "LATITUDE", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;
}
