package br.com.fiap.civia.domain.valueobject;

import br.com.fiap.civia.domain.model.Validacoes;

/**
 * Value Object de endereco (colunas ds_logradouro, nr_endereco e nm_bairro).
 * O numero e opcional (totens ficam em pracas e terminais).
 */
public record Endereco(String logradouro, String numero, String bairro) {

    public Endereco {
        logradouro = Validacoes.textoObrigatorio(logradouro, "logradouro", 120);
        numero = Validacoes.textoOpcional(numero, "numero", 10);
        bairro = Validacoes.textoObrigatorio(bairro, "bairro", 60);
    }

    public String formatado() {
        return logradouro + (numero == null ? "" : ", " + numero) + " - " + bairro;
    }
}
