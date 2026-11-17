package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.Coleta;
import br.com.fiap.civia.domain.model.Entrega;
import br.com.fiap.civia.domain.model.Material;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.Recompensa;
import br.com.fiap.civia.domain.model.Troca;
import br.com.fiap.civia.infrastructure.entity.CartaoEntity;
import br.com.fiap.civia.infrastructure.entity.ColetaEntity;
import br.com.fiap.civia.infrastructure.entity.EntregaEntity;
import br.com.fiap.civia.infrastructure.entity.MaterialEntity;
import br.com.fiap.civia.infrastructure.entity.PostoReciclagemEntity;
import br.com.fiap.civia.infrastructure.entity.RecompensaEntity;
import br.com.fiap.civia.infrastructure.entity.TrocaEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Traducao JPA <-> dominio do modulo de reciclagem (posto, material, cartao, entrega, recompensa, troca e coleta). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReciclagemPersistenceMapper {

    public static PostoReciclagem paraDominio(PostoReciclagemEntity e) {
        return PostoReciclagem.reconstituir(e.getId(), e.getTotemId(), e.getCooperativaId(), e.getCapacidadeKg(),
                e.getLimiteDiarioKg(), e.getStatus());
    }

    public static void copiar(PostoReciclagem p, PostoReciclagemEntity e) {
        e.setTotemId(p.getTotemId());
        e.setCooperativaId(p.getCooperativaId());
        e.setCapacidadeKg(p.getCapacidadeKg());
        e.setLimiteDiarioKg(p.getLimiteDiarioKg());
        e.setStatus(p.getStatus());
    }

    public static Material paraDominio(MaterialEntity e) {
        return Material.reconstituir(e.getId(), e.getNome(), e.getCreditosPorKg(), Boolean.TRUE.equals(e.getAceito()));
    }

    public static void copiar(Material m, MaterialEntity e) {
        e.setNome(m.getNome());
        e.setCreditosPorKg(m.getCreditosPorKg());
        e.setAceito(m.isAceito());
    }

    public static Cartao paraDominio(CartaoEntity e) {
        return Cartao.reconstituir(e.getId(), e.getCodigo(), e.getOrganizacaoEmissoraId(), e.getCartaoAnteriorId(),
                e.getDataEmissao(), e.getStatus(), e.getSaldoCreditos());
    }

    public static void copiar(Cartao c, CartaoEntity e) {
        e.setCodigo(c.getCodigo());
        e.setOrganizacaoEmissoraId(c.getOrganizacaoEmissoraId());
        e.setCartaoAnteriorId(c.getCartaoAnteriorId());
        e.setDataEmissao(c.getDataEmissao());
        e.setStatus(c.getStatus());
        e.setSaldoCreditos(c.getSaldoCreditos());
    }

    public static Entrega paraDominio(EntregaEntity e) {
        return Entrega.reconstituir(e.getId(), e.getCartaoId(), e.getPostoId(), e.getMaterialId(), e.getPesoKg(),
                e.getCreditosGerados(), e.getDataEntrega());
    }

    public static void copiar(Entrega en, EntregaEntity e) {
        e.setCartaoId(en.getCartaoId());
        e.setPostoId(en.getPostoId());
        e.setMaterialId(en.getMaterialId());
        e.setPesoKg(en.getPesoKg());
        e.setCreditosGerados(en.getCreditosGerados());
        e.setDataEntrega(en.getDataEntrega());
    }

    public static Recompensa paraDominio(RecompensaEntity e) {
        return Recompensa.reconstituir(e.getId(), e.getOrganizacaoParceiraId(), e.getNome(), e.getCustoCreditos(),
                Boolean.TRUE.equals(e.getAtivo()));
    }

    public static void copiar(Recompensa r, RecompensaEntity e) {
        e.setOrganizacaoParceiraId(r.getOrganizacaoParceiraId());
        e.setNome(r.getNome());
        e.setCustoCreditos(r.getCustoCreditos());
        e.setAtivo(r.isAtivo());
    }

    public static Troca paraDominio(TrocaEntity e) {
        return Troca.reconstituir(e.getId(), e.getCartaoId(), e.getRecompensaId(), e.getUsuarioId(),
                e.getCreditosUsados(), e.getDataTroca());
    }

    public static void copiar(Troca t, TrocaEntity e) {
        e.setCartaoId(t.getCartaoId());
        e.setRecompensaId(t.getRecompensaId());
        e.setUsuarioId(t.getUsuarioId());
        e.setCreditosUsados(t.getCreditosUsados());
        e.setDataTroca(t.getDataTroca());
    }

    public static Coleta paraDominio(ColetaEntity e) {
        return Coleta.reconstituir(e.getId(), e.getPostoId(), e.getCooperativaId(), e.getPesoTotalKg(), e.getDataColeta());
    }

    public static void copiar(Coleta c, ColetaEntity e) {
        e.setPostoId(c.getPostoId());
        e.setCooperativaId(c.getCooperativaId());
        e.setPesoTotalKg(c.getPesoTotalKg());
        e.setDataColeta(c.getDataColeta());
    }
}
