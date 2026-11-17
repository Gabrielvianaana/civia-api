package br.com.fiap.civia.infrastructure.entity;

import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.infrastructure.converter.HoraConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/** Mapeamento JPA da tabela T_CIVIA_SERVICO (e da tabela associativa T_CIVIA_SERVICO_PERFIL). */
@Entity
@Table(name = "T_CIVIA_SERVICO")
@Getter
@Setter
@NoArgsConstructor
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SERVICO")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoServico tipo;

    @Column(name = "ENDERECO", nullable = false, length = 200)
    private String endereco;

    @Column(name = "LATITUDE", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Convert(converter = HoraConverter.class)
    @Column(name = "HORA_ABERTURA", nullable = false, length = 5)
    private LocalTime horaAbertura;

    @Convert(converter = HoraConverter.class)
    @Column(name = "HORA_FECHAMENTO", nullable = false, length = 5)
    private LocalTime horaFechamento;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "APROVADO", nullable = false)
    private Boolean aprovado;

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "T_CIVIA_SERVICO_PERFIL", joinColumns = @JoinColumn(name = "ID_SERVICO"))
    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", nullable = false, length = 20)
    private Set<PerfilAtendido> perfis = new HashSet<>();
}
