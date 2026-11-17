package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.StatusCartao;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/** Mapeamento JPA de T_CARTAO (cartao anonimo: nenhum dado pessoal - RN07). */
@Entity
@Table(name = "T_CARTAO")
@Getter
@Setter
@NoArgsConstructor
public class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqCartao")
    @SequenceGenerator(name = "sqCartao", sequenceName = "SQ_CARTAO", allocationSize = 1)
    @Column(name = "id_cartao")
    private Long id;

    @Column(name = "cd_cartao", nullable = false, length = 10)
    private String codigo;

    @Column(name = "id_organizacao_emissora", nullable = false)
    private Long organizacaoEmissoraId;

    @Column(name = "id_cartao_anterior")
    private Long cartaoAnteriorId;

    @Column(name = "dt_emissao", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_cartao", nullable = false, length = 10)
    private StatusCartao status;

    @Column(name = "nr_saldo_creditos", nullable = false)
    private Integer saldoCreditos;
}
