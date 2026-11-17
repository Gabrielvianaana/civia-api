package br.com.fiap.civia.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

/**
 * Tipo de servico exibido na tela inicial do totem (RF01). Tabela T_TIPO_SERVICO.
 * O tipo de nome "Abrigo" tem regras proprias: vagas em faixas (RN03) e filtro por perfil (US04).
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TipoServico {

    public static final String NOME_ABRIGO = "ABRIGO";

    @EqualsAndHashCode.Include
    private final Integer id;
    private String nome;
    private String icone;

    private TipoServico(Integer id, String nome, String icone) {
        this.id = id;
        definir(nome, icone);
    }

    public static TipoServico novo(String nome, String icone) {
        return new TipoServico(null, nome, icone);
    }

    public static TipoServico reconstituir(Integer id, String nome, String icone) {
        return new TipoServico(Objects.requireNonNull(id), nome, icone);
    }

    public void atualizar(String nome, String icone) {
        definir(nome, icone);
    }

    public boolean ehAbrigo() {
        return NOME_ABRIGO.equals(normalizar(nome));
    }

    /** Compara nomes ignorando maiusculas e acentos ("Refeicao" = "REFEIÇÃO"). */
    public boolean temNome(String outro) {
        return outro != null && normalizar(nome).equals(normalizar(outro));
    }

    public static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toUpperCase(Locale.ROOT);
    }

    private void definir(String nome, String icone) {
        this.nome = Validacoes.textoObrigatorio(nome, "nome", 40);
        this.icone = Validacoes.textoObrigatorio(icone, "icone", 30);
    }
}
