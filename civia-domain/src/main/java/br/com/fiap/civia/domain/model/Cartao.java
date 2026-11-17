package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.exception.DadoInvalidoException;
import br.com.fiap.civia.domain.exception.RegraNegocioVioladaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Cartao anonimo de reciclagem: so um codigo aleatorio, sem nome, CPF ou foto (RN07). Os creditos nao viram
 * dinheiro, apenas itens do catalogo (RN11). Na perda, o Centro POP emite um cartao novo ligado ao anterior,
 * que e bloqueado e tem o saldo transferido (RN12). Tabela T_CARTAO.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cartao {

    public static final int TAMANHO_CODIGO = 8;
    private static final Pattern CODIGO = Pattern.compile("^[A-Z0-9]{6,10}$");
    private static final int SALDO_MAXIMO = 99_999_999;

    @EqualsAndHashCode.Include
    private final Long id;
    private final String codigo;
    private Long organizacaoEmissoraId;
    private final Long cartaoAnteriorId;
    private final LocalDateTime dataEmissao;
    private StatusCartao status;
    private int saldoCreditos;

    private Cartao(Long id, String codigo, Long organizacaoEmissoraId, Long cartaoAnteriorId, LocalDateTime dataEmissao,
                   StatusCartao status, Integer saldoCreditos) {
        this.id = id;
        if (codigo == null || !CODIGO.matcher(codigo).matches()) {
            throw new DadoInvalidoException("O codigo do cartao deve ter de 6 a 10 letras maiusculas ou numeros.");
        }
        this.codigo = codigo;
        this.organizacaoEmissoraId = Validacoes.obrigatorio(organizacaoEmissoraId, "A organizacao emissora e obrigatoria.");
        this.cartaoAnteriorId = cartaoAnteriorId;
        this.dataEmissao = Validacoes.obrigatorio(dataEmissao, "A data de emissao e obrigatoria.");
        this.status = Validacoes.obrigatorio(status, "O status do cartao e obrigatorio.");
        int saldo = saldoCreditos == null ? 0 : saldoCreditos;
        if (saldo < 0) {
            throw new DadoInvalidoException("O saldo de creditos nao pode ser negativo.");
        }
        if (status == StatusCartao.BLOQUEADO && saldo != 0) {
            throw new DadoInvalidoException("Cartao bloqueado deve ter saldo zero.");
        }
        this.saldoCreditos = saldo;
    }

    /** Cartao novo, emitido pelo Centro POP, ativo e sem saldo. */
    public static Cartao emitir(String codigo, Long organizacaoEmissoraId, LocalDateTime dataEmissao) {
        return new Cartao(null, codigo, organizacaoEmissoraId, null, dataEmissao, StatusCartao.ATIVO, 0);
    }

    /** RN12: o cartao anterior e bloqueado e o saldo dele passa para o cartao novo. */
    public static Cartao substituir(Cartao anterior, String codigo, Long organizacaoEmissoraId, LocalDateTime dataEmissao) {
        Objects.requireNonNull(anterior.getId());
        anterior.garantirAtivo();
        int saldo = anterior.saldoCreditos;
        anterior.saldoCreditos = 0;
        anterior.status = StatusCartao.BLOQUEADO;
        return new Cartao(null, codigo, organizacaoEmissoraId, anterior.getId(), dataEmissao, StatusCartao.ATIVO, saldo);
    }

    public static Cartao reconstituir(Long id, String codigo, Long organizacaoEmissoraId, Long cartaoAnteriorId,
                                      LocalDateTime dataEmissao, StatusCartao status, Integer saldoCreditos) {
        return new Cartao(Objects.requireNonNull(id), codigo, organizacaoEmissoraId, cartaoAnteriorId, dataEmissao,
                status, saldoCreditos);
    }

    /** Alteracao administrativa: emissora e status. Bloquear com saldo perderia creditos: use a substituicao. */
    public void atualizar(Long organizacaoEmissoraId, StatusCartao novoStatus) {
        this.organizacaoEmissoraId = Validacoes.obrigatorio(organizacaoEmissoraId, "A organizacao emissora e obrigatoria.");
        if (novoStatus == StatusCartao.BLOQUEADO && status == StatusCartao.ATIVO && saldoCreditos > 0) {
            throw new RegraNegocioVioladaException("O cartao " + codigo + " tem " + saldoCreditos
                    + " creditos. Para nao perder o saldo, faca a substituicao do cartao no Centro POP (RN12).");
        }
        if (novoStatus != null) {
            this.status = novoStatus;
        }
    }

    public void creditar(int creditos) {
        garantirAtivo();
        if (creditos <= 0) {
            throw new DadoInvalidoException("A quantidade de creditos deve ser positiva.");
        }
        if ((long) saldoCreditos + creditos > SALDO_MAXIMO) {
            throw new RegraNegocioVioladaException("O saldo do cartao " + codigo + " atingiu o limite.");
        }
        saldoCreditos += creditos;
    }

    /** RN11: creditos so saem do cartao em troca por itens do catalogo (ou estorno), nunca ficam negativos. */
    public void debitar(int creditos) {
        garantirAtivo();
        if (creditos <= 0) {
            throw new DadoInvalidoException("A quantidade de creditos deve ser positiva.");
        }
        if (creditos > saldoCreditos) {
            throw new RegraNegocioVioladaException("Saldo insuficiente: o cartao " + codigo + " tem " + saldoCreditos
                    + " creditos e a operacao exige " + creditos + ".");
        }
        saldoCreditos -= creditos;
    }

    public void garantirAtivo() {
        if (status != StatusCartao.ATIVO) {
            throw new RegraNegocioVioladaException("O cartao " + codigo + " esta bloqueado.");
        }
    }
}
