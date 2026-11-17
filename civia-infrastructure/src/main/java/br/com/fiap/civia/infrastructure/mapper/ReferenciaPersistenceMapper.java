package br.com.fiap.civia.infrastructure.mapper;

import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.TipoServico;
import br.com.fiap.civia.infrastructure.entity.OrganizacaoEntity;
import br.com.fiap.civia.infrastructure.entity.PerfilAtendidoEntity;
import br.com.fiap.civia.infrastructure.entity.TipoServicoEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Traducao JPA <-> dominio de organizacao, tipo de servico e perfil atendido. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReferenciaPersistenceMapper {

    public static Organizacao paraDominio(OrganizacaoEntity e) {
        return Organizacao.reconstituir(e.getId(), e.getNome(), e.getTipo(), e.getTelefone(), e.getEmail());
    }

    public static void copiar(Organizacao o, OrganizacaoEntity e) {
        e.setNome(o.getNome());
        e.setTipo(o.getTipo());
        e.setTelefone(o.getTelefone());
        e.setEmail(o.getEmail());
    }

    public static TipoServico paraDominio(TipoServicoEntity e) {
        return TipoServico.reconstituir(e.getId(), e.getNome(), e.getIcone());
    }

    public static void copiar(TipoServico t, TipoServicoEntity e) {
        e.setNome(t.getNome());
        e.setIcone(t.getIcone());
    }

    public static PerfilAtendido paraDominio(PerfilAtendidoEntity e) {
        return PerfilAtendido.reconstituir(e.getId(), e.getDescricao());
    }

    public static void copiar(PerfilAtendido p, PerfilAtendidoEntity e) {
        e.setDescricao(p.getDescricao());
    }
}
