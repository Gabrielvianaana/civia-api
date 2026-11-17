package br.com.fiap.civia.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resultado da consulta no totem: ate 3 servicos, do mais util ao menos util.
 * {@code orientacao} traz o proximo passo (RF06 / RN02): o sistema informa, mas nao reserva vagas.
 */
public record MapaServicosResponse(
        Long totemId,
        String codigoTotem,
        String tipoServicoFiltrado,
        String perfilFiltrado,
        LocalDateTime consultadoEm,
        List<ServicoProximoResponse> servicos,
        String orientacao
) {
}
