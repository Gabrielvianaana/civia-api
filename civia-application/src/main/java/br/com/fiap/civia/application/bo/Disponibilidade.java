package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.StatusDisponibilidade;

import java.time.LocalDateTime;

/**
 * Resultado do calculo de disponibilidade de um servico em um instante.
 *
 * @param status          status exibido no totem (sempre em faixa, nunca o numero de vagas)
 * @param mensagem        texto curto em linguagem simples para o card (RNF02)
 * @param origem          de onde veio a informacao
 * @param dataReferencia  data/hora da atualizacao usada (nula quando o status vem do horario cadastrado)
 */
public record Disponibilidade(
        StatusDisponibilidade status,
        String mensagem,
        Origem origem,
        LocalDateTime dataReferencia
) {

    public enum Origem {
        /** Atualizacao manual do painel com ate 2 horas. */
        ATUALIZACAO_MANUAL,
        /** Calculado a partir do horario de funcionamento cadastrado (RN04). */
        HORARIO_CADASTRADO,
        /** Sem atualizacao nas ultimas 2 horas (RN05). */
        SEM_ATUALIZACAO_RECENTE
    }
}
