package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.infrastructure.converter.SimNaoConverter;
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

import java.time.LocalDateTime;

/** Mapeamento JPA de T_CONSULTA_TOTEM (sem nenhum dado pessoal - RNF07). */
@Entity
@Table(name = "T_CONSULTA_TOTEM")
@Getter
@Setter
@NoArgsConstructor
public class ConsultaTotemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sqConsultaTotem")
    @SequenceGenerator(name = "sqConsultaTotem", sequenceName = "SQ_CONSULTA_TOTEM", allocationSize = 1)
    @Column(name = "id_consulta")
    private Long id;

    @Column(name = "id_totem", nullable = false)
    private Long totemId;

    @Column(name = "id_tipo_servico", nullable = false)
    private Integer tipoServicoId;

    @Column(name = "id_perfil")
    private Integer perfilId;

    @Column(name = "id_servico_escolhido")
    private Long servicoEscolhidoId;

    @Column(name = "dt_consulta", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dataConsulta;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_imprimiu_rota", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean imprimiuRota;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "fl_pediu_abordagem", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean pediuAbordagem;

    @Column(name = "cd_chegada", length = 8)
    private String codigoChegada;

    @Column(name = "dt_chegada", columnDefinition = "DATE")
    private LocalDateTime dataChegada;
}
