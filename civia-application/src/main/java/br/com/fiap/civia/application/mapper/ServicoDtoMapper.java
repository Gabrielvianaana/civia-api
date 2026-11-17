package br.com.fiap.civia.application.mapper;

import br.com.fiap.civia.application.dto.ServicoResponse;
import br.com.fiap.civia.domain.model.Servico;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServicoDtoMapper {

    public static ServicoResponse paraResponse(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getTipo().name(),
                servico.getEndereco(),
                servico.getCoordenada().latitude(),
                servico.getCoordenada().longitude(),
                Conversor.deHora(servico.getHorario().abertura()),
                Conversor.deHora(servico.getHorario().fechamento()),
                servico.getPerfis().stream().map(Enum::name).sorted().toList(),
                servico.isAprovado(),
                servico.getDataCadastro());
    }
}
