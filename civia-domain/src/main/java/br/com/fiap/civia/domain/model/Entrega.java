package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Entrega de material no posto, que gera creditos no cartao. Tabela T_ENTREGA. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entrega {

    /** ck_ent_peso: cada entrega pesa no maximo 10 kg. */
    public static final BigDecimal PESO_MAXIMO_KG = new BigDecimal("10.00");

    @EqualsAndHashCode.Include
    private final Long id;
    private Long cartaoId;
    private Long postoId;
    private Integer materialId;
    private BigDecimal pesoKg;
    private int creditosGerados;
    private final LocalDateTime dataEntrega;

    private Entrega(Long id, Long cartaoId, Long postoId, Integer materialId, BigDecimal pesoKg, Integer creditosGerados,
                    LocalDateTime dataEntrega) {
        this.id = id;
        this.dataEntrega = Validacoes.obrigatorio(dataEntrega, "A data da entrega e obrigatoria.");
        definir(cartaoId, postoId, materialId, pesoKg, creditosGerados);
    }

    /** RN09: so material aceito; os creditos sao calculados pelo peso e pelo valor do material. */
    public static Entrega registrar(Long cartaoId, Long postoId, Material material, BigDecimal pesoKg,
                                    LocalDateTime dataEntrega) {
        BigDecimal peso = validarPeso(pesoKg);
        material.garantirAceito();
        return new Entrega(null, cartaoId, postoId, material.getId(), peso, material.calcularCreditos(peso), dataEntrega);
    }

    public static Entrega reconstituir(Long id, Long cartaoId, Long postoId, Integer materialId, BigDecimal pesoKg,
                                       Integer creditosGerados, LocalDateTime dataEntrega) {
        return new Entrega(Objects.requireNonNull(id), cartaoId, postoId, materialId, pesoKg, creditosGerados, dataEntrega);
    }

    /** Correcao de uma entrega (peso ou material errados): os creditos sao recalculados. */
    public void corrigir(Long cartaoId, Long postoId, Material material, BigDecimal pesoKg) {
        BigDecimal peso = validarPeso(pesoKg);
        material.garantirAceito();
        definir(cartaoId, postoId, material.getId(), peso, material.calcularCreditos(peso));
    }

    private static BigDecimal validarPeso(BigDecimal pesoKg) {
        return Validacoes.decimalPositivo(pesoKg, "pesoKg", PESO_MAXIMO_KG);
    }

    private void definir(Long cartaoId, Long postoId, Integer materialId, BigDecimal pesoKg, Integer creditosGerados) {
        this.cartaoId = Validacoes.obrigatorio(cartaoId, "O cartao da entrega e obrigatorio.");
        this.postoId = Validacoes.obrigatorio(postoId, "O posto da entrega e obrigatorio.");
        this.materialId = Validacoes.obrigatorio(materialId, "O material da entrega e obrigatorio.");
        this.pesoKg = validarPeso(pesoKg);
        if (creditosGerados == null) {
            throw new DadoInvalidoException("Os creditos gerados sao obrigatorios.");
        }
        if (creditosGerados <= 0) {
            throw new RegraNegocioVioladaException("O peso informado e pequeno demais e nao gera creditos.");
        }
        this.creditosGerados = creditosGerados;
    }
}
