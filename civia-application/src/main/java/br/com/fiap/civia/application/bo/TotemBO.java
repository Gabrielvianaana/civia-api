package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.TotemRequest;
import br.com.fiap.civia.application.dto.TotemResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.TotemDtoMapper;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO do cadastro de totens. A identificacao do totem e unica. */
@Service
@RequiredArgsConstructor
public class TotemBO {

    private final TotemDAO totemDAO;

    @Transactional(readOnly = true)
    public List<TotemResponse> listar() {
        return totemDAO.listar().stream().map(TotemDtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public TotemResponse buscar(Long id) {
        return TotemDtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public TotemResponse cadastrar(TotemRequest request) {
        Totem totem = executar(() -> Totem.novo(
                request.identificacao(),
                request.local(),
                Coordenada.de(request.latitude(), request.longitude()),
                request.ativo() == null || request.ativo()));
        if (totemDAO.existePorIdentificacao(totem.getIdentificacao())) {
            throw duplicado(totem.getIdentificacao());
        }
        return TotemDtoMapper.paraResponse(totemDAO.salvar(totem));
    }

    @Transactional
    public TotemResponse atualizar(Long id, TotemRequest request) {
        Totem totem = obter(id);
        executar(() -> totem.atualizar(
                request.identificacao(),
                request.local(),
                Coordenada.de(request.latitude(), request.longitude()),
                request.ativo() == null || request.ativo()));
        if (totemDAO.existePorIdentificacaoEmOutroTotem(totem.getIdentificacao(), id)) {
            throw duplicado(totem.getIdentificacao());
        }
        return TotemDtoMapper.paraResponse(totemDAO.salvar(totem));
    }

    @Transactional
    public void excluir(Long id) {
        obter(id);
        totemDAO.excluir(id);
    }

    private Totem obter(Long id) {
        return totemDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Totem " + id + " nao encontrado."));
    }

    private static ConflitoException duplicado(String identificacao) {
        return new ConflitoException("Ja existe um totem com a identificacao " + identificacao + ".");
    }
}
