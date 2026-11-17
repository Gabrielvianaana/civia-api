package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.AtualizacaoDisponibilidade;
import br.com.fiap.civia.domain.model.ConsultaTotem;
import br.com.fiap.civia.domain.model.Horario;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import br.com.fiap.civia.domain.valueobject.Endereco;
import br.com.fiap.civia.infrastructure.entity.AtualizacaoDisponibilidadeEntity;
import br.com.fiap.civia.infrastructure.entity.ConsultaTotemEntity;
import br.com.fiap.civia.infrastructure.entity.HorarioEntity;
import br.com.fiap.civia.infrastructure.entity.TotemEntity;
import br.com.fiap.civia.infrastructure.entity.UsuarioPainelEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Traducao JPA <-> dominio de horario, usuario do painel, atualizacao, totem e consulta. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OperacaoPersistenceMapper {

    public static Horario paraDominio(HorarioEntity e) {
        return Horario.reconstituir(e.getId(), e.getServicoId(), e.getDiaSemana(), e.getAbertura(), e.getFechamento());
    }

    public static void copiar(Horario h, HorarioEntity e) {
        e.setServicoId(h.getServicoId());
        e.setDiaSemana(h.getDiaSemana());
        e.setAbertura(h.getAbertura());
        e.setFechamento(h.getFechamento());
    }

    public static UsuarioPainel paraDominio(UsuarioPainelEntity e) {
        return UsuarioPainel.reconstituir(e.getId(), e.getOrganizacaoId(), e.getNome(), e.getEmail(),
                e.getPerfilAcesso(), Boolean.TRUE.equals(e.getAtivo()));
    }

    public static void copiar(UsuarioPainel u, UsuarioPainelEntity e) {
        e.setOrganizacaoId(u.getOrganizacaoId());
        e.setNome(u.getNome());
        e.setEmail(u.getEmail());
        e.setPerfilAcesso(u.getPerfilAcesso());
        e.setAtivo(u.isAtivo());
    }

    public static AtualizacaoDisponibilidade paraDominio(AtualizacaoDisponibilidadeEntity e) {
        return AtualizacaoDisponibilidade.reconstituir(e.getId(), e.getServicoId(), e.getUsuarioId(), e.getStatus(),
                e.getVagas(), e.getOrigem(), e.getDataHora());
    }

    public static void copiar(AtualizacaoDisponibilidade a, AtualizacaoDisponibilidadeEntity e) {
        e.setServicoId(a.getServicoId());
        e.setUsuarioId(a.getUsuarioId());
        e.setStatus(a.getStatus());
        e.setVagas(a.getVagas());
        e.setOrigem(a.getOrigem());
        e.setDataHora(a.getDataHora());
    }

    public static Totem paraDominio(TotemEntity e) {
        return Totem.reconstituir(e.getId(), e.getCodigo(), new Endereco(e.getLogradouro(), null, e.getBairro()),
                CoordenadaMapper.paraDominio(e.getLatitude(), e.getLongitude()), e.getStatus(),
                Boolean.TRUE.equals(e.getPapelImpressora()), e.getDataInstalacao(), e.getUltimaSincronizacao());
    }

    public static void copiar(Totem t, TotemEntity e) {
        e.setCodigo(t.getCodigo());
        e.setLogradouro(t.getEndereco().logradouro());
        e.setBairro(t.getEndereco().bairro());
        e.setLatitude(CoordenadaMapper.paraDecimal(t.getCoordenada().latitude()));
        e.setLongitude(CoordenadaMapper.paraDecimal(t.getCoordenada().longitude()));
        e.setStatus(t.getStatus());
        e.setPapelImpressora(t.isPapelImpressora());
        e.setDataInstalacao(t.getDataInstalacao());
        e.setUltimaSincronizacao(t.getUltimaSincronizacao());
    }

    public static ConsultaTotem paraDominio(ConsultaTotemEntity e) {
        return ConsultaTotem.reconstituir(e.getId(), e.getTotemId(), e.getTipoServicoId(), e.getPerfilId(),
                e.getServicoEscolhidoId(), e.getDataConsulta(), Boolean.TRUE.equals(e.getImprimiuRota()),
                Boolean.TRUE.equals(e.getPediuAbordagem()), e.getCodigoChegada(), e.getDataChegada());
    }

    public static void copiar(ConsultaTotem c, ConsultaTotemEntity e) {
        e.setTotemId(c.getTotemId());
        e.setTipoServicoId(c.getTipoServicoId());
        e.setPerfilId(c.getPerfilId());
        e.setServicoEscolhidoId(c.getServicoEscolhidoId());
        e.setDataConsulta(c.getDataConsulta());
        e.setImprimiuRota(c.isImprimiuRota());
        e.setPediuAbordagem(c.isPediuAbordagem());
        e.setCodigoChegada(c.getCodigoChegada());
        e.setDataChegada(c.getDataChegada());
    }
}
