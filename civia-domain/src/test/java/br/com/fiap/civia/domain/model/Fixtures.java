package br.com.fiap.civia.domain.model;

import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;

import java.time.LocalDateTime;
import java.util.Set;

/** Objetos de dominio prontos para os testes. */
final class Fixtures {

    static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 19, 0); // quarta-feira
    static final TipoServico ABRIGO = TipoServico.reconstituir(1, "Abrigo", "cama");
    static final TipoServico REFEICAO = TipoServico.reconstituir(2, "Refeição", "prato");
    static final PerfilAtendido MULHER = PerfilAtendido.reconstituir(2, "Mulher");

    private Fixtures() {
    }

    static Servico servico(long id, TipoServico tipo, Integer capacidade, long organizacaoId) {
        return Servico.reconstituir(id, organizacaoId, tipo, "Servico " + id, new Endereco("Rua X", "1", "Se"),
                new Coordenada(-23.55, -46.63), capacidade, false, StatusAprovacao.APROVADO, AGORA.minusDays(1),
                Set.of(MULHER));
    }
}
