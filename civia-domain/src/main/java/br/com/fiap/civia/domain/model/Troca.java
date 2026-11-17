package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

/** Troca de creditos do cartao por um item do catalogo, registrada pela equipe (RN11). Tabela T_TROCA. */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Troca {

    @EqualsAndHashCode.Include
    private final Long id;
    private Long cartaoId;
    private Long recompensaId;
    private Long usuarioId;
    private int creditosUsados;
    private final LocalDateTime dataTroca;

    private Troca(Long id, Long cartaoId, Long recompensaId, Long usuarioId, Integer creditosUsados,
                  LocalDateTime dataTroca) {
        this.id = id;
        this.dataTroca = Validacoes.obrigatorio(dataTroca, "A data da troca e obrigatoria.");
        definir(cartaoId, recompensaId, usuarioId, creditosUsados);
    }

    /** Os creditos usados sao o custo da recompensa no momento da troca. */
    public static Troca registrar(Long cartaoId, Recompensa recompensa, Long usuarioId, LocalDateTime dataTroca) {
        recompensa.garantirDisponivel();
        return new Troca(null, cartaoId, recompensa.getId(), usuarioId, recompensa.getCustoCreditos(), dataTroca);
    }

    public static Troca reconstituir(Long id, Long cartaoId, Long recompensaId, Long usuarioId, Integer creditosUsados,
                                     LocalDateTime dataTroca) {
        return new Troca(Objects.requireNonNull(id), cartaoId, recompensaId, usuarioId, creditosUsados, dataTroca);
    }

    public void corrigir(Long cartaoId, Recompensa recompensa, Long usuarioId) {
        recompensa.garantirDisponivel();
        definir(cartaoId, recompensa.getId(), usuarioId, recompensa.getCustoCreditos());
    }

    private void definir(Long cartaoId, Long recompensaId, Long usuarioId, Integer creditosUsados) {
        this.cartaoId = Validacoes.obrigatorio(cartaoId, "O cartao da troca e obrigatorio.");
        this.recompensaId = Validacoes.obrigatorio(recompensaId, "A recompensa da troca e obrigatoria.");
        this.usuarioId = Validacoes.obrigatorio(usuarioId, "O usuario que registrou a troca e obrigatorio.");
        if (creditosUsados == null || creditosUsados <= 0) {
            throw new DadoInvalidoException("Os creditos usados devem ser positivos.");
        }
        this.creditosUsados = creditosUsados;
    }
}
