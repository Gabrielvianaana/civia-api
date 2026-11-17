package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Posto de reciclagem acoplado a um totem (um posto por totem) e atendido por uma cooperativa.
 * Tabela T_POSTO_RECICLAGEM.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostoReciclagem {

    /** Limite diario padrao de material por cartao (RN08), igual ao DEFAULT da coluna. */
    public static final BigDecimal LIMITE_DIARIO_PADRAO_KG = new BigDecimal("10.00");

    @EqualsAndHashCode.Include
    private final Long id;
    private Long totemId;
    private Long cooperativaId;
    private BigDecimal capacidadeKg;
    private BigDecimal limiteDiarioKg;
    private StatusPosto status;

    private PostoReciclagem(Long id, Long totemId, Long cooperativaId, BigDecimal capacidadeKg,
                            BigDecimal limiteDiarioKg, StatusPosto status) {
        this.id = id;
        definir(totemId, cooperativaId, capacidadeKg, limiteDiarioKg, status);
    }

    public static PostoReciclagem novo(Long totemId, Long cooperativaId, BigDecimal capacidadeKg,
                                       BigDecimal limiteDiarioKg, StatusPosto status) {
        return new PostoReciclagem(null, totemId, cooperativaId, capacidadeKg,
                limiteDiarioKg == null ? LIMITE_DIARIO_PADRAO_KG : limiteDiarioKg,
                status == null ? StatusPosto.ATIVO : status);
    }

    public static PostoReciclagem reconstituir(Long id, Long totemId, Long cooperativaId, BigDecimal capacidadeKg,
                                               BigDecimal limiteDiarioKg, StatusPosto status) {
        return new PostoReciclagem(Objects.requireNonNull(id), totemId, cooperativaId, capacidadeKg, limiteDiarioKg, status);
    }

    public void atualizar(Long totemId, Long cooperativaId, BigDecimal capacidadeKg, BigDecimal limiteDiarioKg,
                          StatusPosto status) {
        definir(totemId, cooperativaId, capacidadeKg, limiteDiarioKg == null ? this.limiteDiarioKg : limiteDiarioKg,
                status == null ? this.status : status);
    }

    /** So posto ATIVO recebe material: CHEIO aguarda a coleta e MANUTENCAO esta parado. */
    public void garantirRecebeEntregas() {
        if (status != StatusPosto.ATIVO) {
            throw new RegraNegocioVioladaException("O posto " + id + " esta " + status + " e nao recebe entregas no momento.");
        }
    }

    /** O material ja guardado no posto mais a nova entrega nao pode passar da capacidade. */
    public void garantirCabe(BigDecimal acumuladoKg, BigDecimal pesoKg) {
        BigDecimal disponivel = capacidadeKg.subtract(acumuladoKg).max(BigDecimal.ZERO);
        if (pesoKg.compareTo(disponivel) > 0) {
            throw new RegraNegocioVioladaException("O posto " + id + " tem espaco para apenas " + disponivel
                    + " kg ate a proxima coleta.");
        }
    }

    /** Atingiu a capacidade: passa a CHEIO ate a cooperativa coletar. */
    public void atualizarLotacao(BigDecimal acumuladoKg) {
        if (status == StatusPosto.ATIVO && acumuladoKg.compareTo(capacidadeKg) >= 0) {
            status = StatusPosto.CHEIO;
        }
    }

    /** Depois da coleta o posto volta a receber material. */
    public void liberarAposColeta() {
        if (status == StatusPosto.CHEIO) {
            status = StatusPosto.ATIVO;
        }
    }

    private void definir(Long totemId, Long cooperativaId, BigDecimal capacidadeKg, BigDecimal limiteDiarioKg,
                         StatusPosto status) {
        this.totemId = Validacoes.obrigatorio(totemId, "O totem do posto e obrigatorio.");
        this.cooperativaId = Validacoes.obrigatorio(cooperativaId, "A cooperativa do posto e obrigatoria.");
        this.capacidadeKg = Validacoes.decimalPositivo(capacidadeKg, "capacidadeKg", new BigDecimal("9999.99"));
        this.limiteDiarioKg = Validacoes.decimalPositivo(limiteDiarioKg, "limiteDiarioKg", new BigDecimal("999.99"));
        this.status = Validacoes.obrigatorio(status, "O status do posto e obrigatorio.");
    }
}
