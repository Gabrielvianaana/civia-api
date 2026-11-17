package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ServicoResponse(
        Long id,
        Long organizacaoId,
        TipoServicoResponse tipoServico,
        String nome,
        String logradouro,
        String numero,
        String bairro,
        double latitude,
        double longitude,
        Integer capacidade,
        boolean funciona24h,
        String statusAprovacao,
        LocalDateTime dataCadastro,
        List<PerfilAtendidoResponse> perfis
) {
}
