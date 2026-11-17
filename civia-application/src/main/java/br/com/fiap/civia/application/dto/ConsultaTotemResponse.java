package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resultado da consulta feita em um totem: ate 3 servicos, do mais util ao menos util.
 * {@code orientacao} traz o proximo passo (RF06 / RN02): o sistema informa, mas nao reserva vagas.
 */
public record ConsultaTotemResponse(
        Long totemId,
        String identificacaoTotem,
        String localTotem,
        String tipoFiltrado,
        String perfilFiltrado,
        LocalDateTime consultadoEm,
        List<ServicoProximoResponse> servicos,
        String orientacao
) {
}
