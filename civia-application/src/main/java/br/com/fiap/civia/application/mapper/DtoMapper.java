package br.com.fiap.civia.application.mapper;

import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
import br.com.fiap.civia.application.dto.TrocaResponse;
import br.com.fiap.civia.application.dto.RecompensaResponse;
import br.com.fiap.civia.application.dto.MaterialResponse;
import br.com.fiap.civia.application.dto.EntregaResponse;
import br.com.fiap.civia.application.dto.ColetaResponse;
import br.com.fiap.civia.application.dto.CartaoResponse;
import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
import br.com.fiap.civia.application.dto.HorarioResponse;
import br.com.fiap.civia.application.dto.OrganizacaoResponse;
import br.com.fiap.civia.application.dto.PerfilAtendidoResponse;
import br.com.fiap.civia.application.dto.ServicoResponse;
import br.com.fiap.civia.application.dto.TipoServicoResponse;
import br.com.fiap.civia.application.dto.TotemResponse;
import br.com.fiap.civia.application.dto.UsuarioPainelResponse;
import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.Troca;
import br.com.fiap.civia.domain.model.Recompensa;
import br.com.fiap.civia.domain.model.Material;
import br.com.fiap.civia.domain.model.Entrega;
import br.com.fiap.civia.domain.model.Coleta;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.ConsultaTotem;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.Servico;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Converte o modelo de dominio nos DTOs de saida da API. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DtoMapper {

    public static OrganizacaoResponse paraResponse(Organizacao o) {
        return new OrganizacaoResponse(o.getId(), o.getNome(), o.getTipo().name(), o.getTelefone(), o.getEmail());
    }

    public static TipoServicoResponse paraResponse(TipoServico t) {
        return new TipoServicoResponse(t.getId(), t.getNome(), t.getIcone(), t.ehAbrigo());
    }

    public static PerfilAtendidoResponse paraResponse(PerfilAtendido p) {
        return new PerfilAtendidoResponse(p.getId(), p.getDescricao());
    }

    public static ServicoResponse paraResponse(Servico s) {
        return new ServicoResponse(
                s.getId(), s.getOrganizacaoId(), paraResponse(s.getTipo()), s.getNome(),
                s.getEndereco().logradouro(), s.getEndereco().numero(), s.getEndereco().bairro(),
                s.getCoordenada().latitude(), s.getCoordenada().longitude(), s.getCapacidade(), s.isFunciona24h(),
                s.getStatusAprovacao().name(), s.getDataCadastro(),
                s.getPerfis().stream().map(DtoMapper::paraResponse).toList());
    }

    public static HorarioResponse paraResponse(Horario h) {
        return new HorarioResponse(h.getId(), h.getServicoId(), h.getDiaSemana(), Horario.nomeDoDia(h.getDiaSemana()),
                Conversor.deHora(h.getAbertura()), Conversor.deHora(h.getFechamento()));
    }

    public static UsuarioPainelResponse paraResponse(UsuarioPainel u) {
        return new UsuarioPainelResponse(u.getId(), u.getOrganizacaoId(), u.getNome(), u.getEmail(),
                u.getPerfilAcesso().name(), u.isAtivo());
    }

    public static AtualizacaoDisponibilidadeResponse paraResponse(AtualizacaoDisponibilidade a) {
        return new AtualizacaoDisponibilidadeResponse(a.getId(), a.getServicoId(), a.getUsuarioId(), a.getStatus().name(),
                a.getVagas(), a.getOrigem().name(), a.getDataHora());
    }

    public static TotemResponse paraResponse(Totem t) {
        return new TotemResponse(t.getId(), t.getCodigo(), t.getEndereco().logradouro(), t.getEndereco().bairro(),
                t.getCoordenada().latitude(), t.getCoordenada().longitude(), t.getStatus().name(),
                t.isPapelImpressora(), t.getDataInstalacao(), t.getUltimaSincronizacao());
    }

    public static ConsultaTotemResponse paraResponse(ConsultaTotem c) {
        return new ConsultaTotemResponse(c.getId(), c.getTotemId(), c.getTipoServicoId(), c.getPerfilId(),
                c.getServicoEscolhidoId(), c.getDataConsulta(), c.isImprimiuRota(), c.isPediuAbordagem(),
                c.getCodigoChegada(), c.getDataChegada());
    }

    public static MaterialResponse paraResponse(Material m) {
        return new MaterialResponse(m.getId(), m.getNome(), m.getCreditosPorKg(), m.isAceito());
    }

    public static CartaoResponse paraResponse(Cartao c) {
        return new CartaoResponse(c.getId(), c.getCodigo(), c.getOrganizacaoEmissoraId(), c.getCartaoAnteriorId(),
                c.getDataEmissao(), c.getStatus().name(), c.getSaldoCreditos());
    }

    public static EntregaResponse paraResponse(Entrega e) {
        return new EntregaResponse(e.getId(), e.getCartaoId(), e.getPostoId(), e.getMaterialId(), e.getPesoKg(),
                e.getCreditosGerados(), e.getDataEntrega());
    }

    public static RecompensaResponse paraResponse(Recompensa r) {
        return new RecompensaResponse(r.getId(), r.getOrganizacaoParceiraId(), r.getNome(), r.getCustoCreditos(), r.isAtivo());
    }

    public static TrocaResponse paraResponse(Troca t) {
        return new TrocaResponse(t.getId(), t.getCartaoId(), t.getRecompensaId(), t.getUsuarioId(), t.getCreditosUsados(),
                t.getDataTroca());
    }

    public static ColetaResponse paraResponse(Coleta c) {
        return new ColetaResponse(c.getId(), c.getPostoId(), c.getCooperativaId(), c.getPesoTotalKg(), c.getDataColeta());
    }
}
