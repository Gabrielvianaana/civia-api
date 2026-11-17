package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.TipoOrganizacao;
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

/** Mapeamento JPA de T_ORGANIZACAO. */
@Entity
@Table(name = "T_ORGANIZACAO")
@Getter
@Setter
@NoArgsConstructor
public class OrganizacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqOrganizacao")
    @SequenceGenerator(name = "sqOrganizacao", sequenceName = "SQ_ORGANIZACAO", allocationSize = 1)
    @Column(name = "id_organizacao")
    private Long id;

    @Column(name = "nm_organizacao", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_organizacao", nullable = false, length = 20)
    private TipoOrganizacao tipo;

    @Column(name = "nr_telefone", length = 15)
    private String telefone;

    @Column(name = "ds_email", length = 120)
    private String email;
}
