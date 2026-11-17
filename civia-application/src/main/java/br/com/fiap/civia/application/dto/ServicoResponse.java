package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Representacao de saida do recurso servico. */
public record ServicoResponse(
        Long id,
        String nome,
        String tipo,
        String endereco,
        double latitude,
        double longitude,
        String horaAbertura,
        String horaFechamento,
        List<String> perfis,
        boolean aprovado,
        LocalDateTime dataCadastro
) {
}
