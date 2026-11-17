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
 * Registro de uma consulta feita no totem, sem nenhum dado pessoal (RNF07). Tabela T_CONSULTA_TOTEM.
 * Quando a rota e impressa, o papel traz um codigo de chegada; o servico registra a chegada com esse codigo
 * (RF12 / US14), alimentando o indicador de chegadas via totem.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConsultaTotem {

    public static final int TAMANHO_CODIGO = 8;
    private static final Pattern FORMATO_CODIGO = Pattern.compile("^[A-Z0-9]{8}$");

    @EqualsAndHashCode.Include
    private final Long id;
    private Long totemId;
    private Integer tipoServicoId;
    private Integer perfilId;
    private Long servicoEscolhidoId;
    private final LocalDateTime dataConsulta;
    private boolean imprimiuRota;
    private boolean pediuAbordagem;
    private String codigoChegada;
    private LocalDateTime dataChegada;

    private ConsultaTotem(Long id, Long totemId, Integer tipoServicoId, Integer perfilId, Long servicoEscolhidoId,
                          LocalDateTime dataConsulta, boolean imprimiuRota, boolean pediuAbordagem,
                          String codigoChegada, LocalDateTime dataChegada) {
        this.id = id;
        this.dataConsulta = Validacoes.obrigatorio(dataConsulta, "A data da consulta e obrigatoria.");
        this.dataChegada = dataChegada;
        definir(totemId, tipoServicoId, perfilId, servicoEscolhidoId, imprimiuRota, pediuAbordagem, codigoChegada);
    }

    /**
     * Registra uma consulta. Se a rota foi impressa, recebe o codigo de chegada gerado (8 caracteres).
     */
    public static ConsultaTotem registrar(Long totemId, Integer tipoServicoId, Integer perfilId, Long servicoEscolhidoId,
                                          LocalDateTime dataConsulta, boolean imprimiuRota, boolean pediuAbordagem,
                                          String codigoGerado) {
        exigirServicoParaImprimir(imprimiuRota, servicoEscolhidoId, codigoGerado);
        return new ConsultaTotem(null, totemId, tipoServicoId, perfilId, servicoEscolhidoId, dataConsulta, imprimiuRota,
                pediuAbordagem, imprimiuRota ? codigoGerado : null, null);
    }

    public static ConsultaTotem reconstituir(Long id, Long totemId, Integer tipoServicoId, Integer perfilId,
                                             Long servicoEscolhidoId, LocalDateTime dataConsulta, boolean imprimiuRota,
                                             boolean pediuAbordagem, String codigoChegada, LocalDateTime dataChegada) {
        return new ConsultaTotem(Objects.requireNonNull(id), totemId, tipoServicoId, perfilId, servicoEscolhidoId,
                dataConsulta, imprimiuRota, pediuAbordagem, codigoChegada, dataChegada);
    }

    /**
     * Corrige os dados da consulta. Ao passar a imprimir a rota, usa o codigo gerado;
     * deixar de imprimir so e possivel antes da chegada ser registrada.
     */
    public void atualizar(Long totemId, Integer tipoServicoId, Integer perfilId, Long servicoEscolhidoId,
                          boolean imprimiuRota, boolean pediuAbordagem, String codigoGerado) {
        String codigo = codigoChegada;
        if (imprimiuRota && codigo == null) {
            codigo = codigoGerado;
        }
        exigirServicoParaImprimir(imprimiuRota, servicoEscolhidoId, codigo);
        if (!imprimiuRota) {
            if (dataChegada != null) {
                throw new RegraNegocioVioladaException("A chegada ja foi registrada: a consulta precisa manter a rota impressa.");
            }
            codigo = null;
        }
        definir(totemId, tipoServicoId, perfilId, servicoEscolhidoId, imprimiuRota, pediuAbordagem, codigo);
    }

    /** US14: o servico informa o codigo do papel e a chegada e registrada (uma unica vez). */
    public void registrarChegada(LocalDateTime instante) {
        if (codigoChegada == null) {
            throw new RegraNegocioVioladaException("Esta consulta nao imprimiu rota, entao nao possui codigo de chegada.");
        }
        if (dataChegada != null) {
            throw new RegraNegocioVioladaException("A chegada do codigo " + codigoChegada + " ja foi registrada.");
        }
        if (instante.isBefore(dataConsulta)) {
            throw new RegraNegocioVioladaException("A chegada nao pode ser anterior a consulta.");
        }
        this.dataChegada = instante;
    }

    public boolean chegadaRegistrada() {
        return dataChegada != null;
    }

    /** A rota impressa leva ao servico escolhido e traz o codigo de chegada. */
    private static void exigirServicoParaImprimir(boolean imprimiuRota, Long servicoEscolhidoId, String codigo) {
        if (imprimiuRota && servicoEscolhidoId == null) {
            throw new RegraNegocioVioladaException("Para imprimir a rota, informe o servico escolhido.");
        }
        if (imprimiuRota && codigo == null) {
            throw new RegraNegocioVioladaException("Rota impressa exige codigo de chegada.");
        }
    }

    private void definir(Long totemId, Integer tipoServicoId, Integer perfilId, Long servicoEscolhidoId,
                         boolean imprimiuRota, boolean pediuAbordagem, String codigoChegada) {
        this.totemId = Validacoes.obrigatorio(totemId, "O totem da consulta e obrigatorio.");
        this.tipoServicoId = Validacoes.obrigatorio(tipoServicoId, "O tipo de servico consultado e obrigatorio.");
        this.perfilId = perfilId;
        this.servicoEscolhidoId = servicoEscolhidoId;
        if (codigoChegada != null && !FORMATO_CODIGO.matcher(codigoChegada).matches()) {
            throw new DadoInvalidoException("O codigo de chegada deve ter 8 letras maiusculas ou numeros.");
        }
        // ck_cons_codigo: so existe codigo de chegada quando a rota foi impressa
        if (codigoChegada != null && !imprimiuRota) {
            throw new RegraNegocioVioladaException("O codigo de chegada so existe quando a rota e impressa.");
        }
        this.imprimiuRota = imprimiuRota;
        this.pediuAbordagem = pediuAbordagem;
        this.codigoChegada = codigoChegada;
    }
}
