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

/** Mapeamento JPA de T_PERFIL_ATENDIDO. */
@Entity
@Table(name = "T_PERFIL_ATENDIDO")
@Getter
@Setter
@NoArgsConstructor
public class PerfilAtendidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqPerfilAtendido")
    @SequenceGenerator(name = "sqPerfilAtendido", sequenceName = "SQ_PERFIL_ATENDIDO", allocationSize = 1)
    @Column(name = "id_perfil")
    private Integer id;

    @Column(name = "ds_perfil", nullable = false, length = 40)
    private String descricao;
}
