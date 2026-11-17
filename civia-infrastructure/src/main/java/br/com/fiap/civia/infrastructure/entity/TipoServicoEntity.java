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

/** Mapeamento JPA de T_TIPO_SERVICO. */
@Entity
@Table(name = "T_TIPO_SERVICO")
@Getter
@Setter
@NoArgsConstructor
public class TipoServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqTipoServico")
    @SequenceGenerator(name = "sqTipoServico", sequenceName = "SQ_TIPO_SERVICO", allocationSize = 1)
    @Column(name = "id_tipo_servico")
    private Integer id;

    @Column(name = "nm_tipo_servico", nullable = false, length = 40)
    private String nome;

    @Column(name = "ds_icone", nullable = false, length = 30)
    private String icone;
}
