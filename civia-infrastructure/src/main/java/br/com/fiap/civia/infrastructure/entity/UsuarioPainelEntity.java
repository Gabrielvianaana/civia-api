package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.PerfilAcesso;
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

/** Mapeamento JPA de T_USUARIO_PAINEL. */
@Entity
@Table(name = "T_USUARIO_PAINEL")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioPainelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqUsuarioPainel")
    @SequenceGenerator(name = "sqUsuarioPainel", sequenceName = "SQ_USUARIO_PAINEL", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "id_organizacao", nullable = false)
    private Long organizacaoId;

    @Column(name = "nm_usuario", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_email", nullable = false, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_perfil_acesso", nullable = false, length = 12)
    private PerfilAcesso perfilAcesso;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_ativo", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean ativo;
}
