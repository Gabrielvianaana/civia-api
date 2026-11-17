package br.com.fiap.civia.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Coleta do material de um posto feita pela cooperativa responsavel. Tabela T_COLETA. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coleta {

    @EqualsAndHashCode.Include
    private final Long id;
    private Long postoId;
    private Long cooperativaId;
    private BigDecimal pesoTotalKg;
    private LocalDateTime dataColeta;

    private Coleta(Long id, Long postoId, Long cooperativaId, BigDecimal pesoTotalKg, LocalDateTime dataColeta) {
        this.id = id;
        definir(postoId, cooperativaId, pesoTotalKg, dataColeta);
    }

    public static Coleta registrar(Long postoId, Long cooperativaId, BigDecimal pesoTotalKg, LocalDateTime dataColeta) {
        return new Coleta(null, postoId, cooperativaId, pesoTotalKg, dataColeta);
    }

    public static Coleta reconstituir(Long id, Long postoId, Long cooperativaId, BigDecimal pesoTotalKg,
                                      LocalDateTime dataColeta) {
        return new Coleta(Objects.requireNonNull(id), postoId, cooperativaId, pesoTotalKg, dataColeta);
    }

    public void atualizar(Long postoId, Long cooperativaId, BigDecimal pesoTotalKg, LocalDateTime dataColeta) {
        definir(postoId, cooperativaId, pesoTotalKg, dataColeta == null ? this.dataColeta : dataColeta);
    }

    private void definir(Long postoId, Long cooperativaId, BigDecimal pesoTotalKg, LocalDateTime dataColeta) {
        this.postoId = Validacoes.obrigatorio(postoId, "O posto da coleta e obrigatorio.");
        this.cooperativaId = Validacoes.obrigatorio(cooperativaId, "A cooperativa da coleta e obrigatoria.");
        this.pesoTotalKg = Validacoes.decimalPositivo(pesoTotalKg, "pesoTotalKg", new BigDecimal("99999.99"));
        this.dataColeta = Validacoes.obrigatorio(dataColeta, "A data da coleta e obrigatoria.");
    }
}
