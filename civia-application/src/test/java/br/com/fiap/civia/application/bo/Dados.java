package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.OrigemAtualizacao;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.StatusAprovacao;
import br.com.fiap.civia.domain.model.StatusDisponibilidade;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

/** Dados simulados para os testes dos BOs. */
final class Dados {

    static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    /** Quarta-feira, 19h00 em Sao Paulo (dia 4 no padrao da tabela T_HORARIO). */
    static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 19, 0);
    static final Clock RELOGIO = Clock.fixed(AGORA.atZone(SP).toInstant(), SP);
    static final Coordenada PRACA_DA_SE = new Coordenada(-23.550520, -46.633308);

    static final TipoServico ABRIGO = TipoServico.reconstituir(1, "Abrigo", "cama");
    static final TipoServico REFEICAO = TipoServico.reconstituir(2, "Refeicao", "prato");
    static final TipoServico BANHO = TipoServico.reconstituir(3, "Banho", "chuveiro");
    static final TipoServico ATENDIMENTO = TipoServico.reconstituir(4, "Atendimento", "pessoa");
    static final PerfilAtendido HOMEM = PerfilAtendido.reconstituir(1, "Homem");
    static final PerfilAtendido MULHER = PerfilAtendido.reconstituir(2, "Mulher");

    private Dados() {
    }

    /** Servico aprovado a {@code metrosAoSul} metros ao sul da Praca da Se (0,001 grau ~ 111 m). */
    static Servico servico(long id, TipoServico tipo, double metrosAoSul, StatusAprovacao status, PerfilAtendido... perfis) {
        double lat = PRACA_DA_SE.latitude() - metrosAoSul / 111_195d;
        return Servico.reconstituir(id, 1L, tipo, "Servico " + id, new Endereco("Rua " + id, null, "Se"),
                new Coordenada(lat, PRACA_DA_SE.longitude()), null, false, status, AGORA.minusDays(30),
                perfis.length == 0 ? Set.of(HOMEM) : Set.of(perfis));
    }

    static Servico servico(long id, TipoServico tipo, double metrosAoSul, PerfilAtendido... perfis) {
        return servico(id, tipo, metrosAoSul, StatusAprovacao.APROVADO, perfis);
    }

    static AtualizacaoDisponibilidade vagas(long servicoId, int vagas, LocalDateTime quando) {
        return AtualizacaoDisponibilidade.reconstituir(100 + servicoId, servicoId, null, DisponibilidadeBO.faixaDeVagas(vagas),
                vagas, OrigemAtualizacao.CENTRAL_VAGAS, quando);
    }

    static AtualizacaoDisponibilidade situacao(long servicoId, StatusDisponibilidade status, LocalDateTime quando) {
        return AtualizacaoDisponibilidade.reconstituir(200 + servicoId, servicoId, 1L, status,
                status == StatusDisponibilidade.LOTADO ? 0 : null, OrigemAtualizacao.PAINEL, quando);
    }

    /** Faixa de quarta-feira (dia 4). */
    static Horario quarta(long servicoId, String abertura, String fechamento) {
        return Horario.reconstituir(servicoId * 10, servicoId, 4, LocalTime.parse(abertura), LocalTime.parse(fechamento));
    }
}
