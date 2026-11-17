package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.SituacaoServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Mapeamento JPA da tabela T_CIVIA_ATUALIZACAO_DISP.
 * A FK para T_CIVIA_SERVICO (com ON DELETE CASCADE) esta no DDL; aqui o servico e referenciado pelo id,
 * como recomenda o DDD para referencias entre agregados.
 */
@Entity
@Table(name = "T_CIVIA_ATUALIZACAO_DISP")
@Getter
@Setter
@NoArgsConstructor
public class AtualizacaoDisponibilidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ATUALIZACAO")
    private Long id;

    @Column(name = "ID_SERVICO", nullable = false)
    private Long servicoId;

    @Column(name = "VAGAS")
    private Integer vagas;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", length = 20)
    private SituacaoServico situacao;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;
}
