package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.TotemRequest;
import br.com.fiap.civia.application.dto.TotemResponse;
import br.com.fiap.civia.application.exception.ConflitoException;
import br.com.fiap.civia.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.civia.application.mapper.Conversor;
import br.com.fiap.civia.application.mapper.DtoMapper;
import br.com.fiap.civia.domain.dao.ConsultaTotemDAO;
import br.com.fiap.civia.domain.dao.TotemDAO;
import br.com.fiap.civia.domain.model.StatusTotem;
import br.com.fiap.civia.domain.model.Totem;
import br.com.fiap.civia.domain.valueobject.Coordenada;
import br.com.fiap.civia.domain.valueobject.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static br.com.fiap.civia.application.exception.TradutorExcecaoDominio.executar;

/** BO dos totens (T_TOTEM). Codigo unico; totem com consultas registradas nao e excluido. */
@Service
@RequiredArgsConstructor
public class TotemBO {

    private final TotemDAO totemDAO;
    private final ConsultaTotemDAO consultaDAO;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<TotemResponse> listar() {
        return totemDAO.listar().stream().map(DtoMapper::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public TotemResponse buscar(Long id) {
        return DtoMapper.paraResponse(obter(id));
    }

    @Transactional
    public TotemResponse cadastrar(TotemRequest r) {
        Totem t = executar(() -> Totem.novo(r.codigo(), new Endereco(r.logradouro(), null, r.bairro()),
                Coordenada.de(r.latitude(), r.longitude()), status(r.status()),
                r.papelImpressora() == null || r.papelImpressora(),
                r.dataInstalacao() == null ? LocalDate.now(clock) : r.dataInstalacao(), r.ultimaSincronizacao()));
        garantirCodigoUnico(t.getCodigo(), null);
        return DtoMapper.paraResponse(totemDAO.salvar(t));
    }

    @Transactional
    public TotemResponse atualizar(Long id, TotemRequest r) {
        Totem t = obter(id);
        executar(() -> t.atualizar(r.codigo(), new Endereco(r.logradouro(), null, r.bairro()),
                Coordenada.de(r.latitude(), r.longitude()), status(r.status()),
                r.papelImpressora() == null || r.papelImpressora(),
                r.dataInstalacao() == null ? t.getDataInstalacao() : r.dataInstalacao(),
                r.ultimaSincronizacao() == null ? t.getUltimaSincronizacao() : r.ultimaSincronizacao()));
        garantirCodigoUnico(t.getCodigo(), id);
        return DtoMapper.paraResponse(totemDAO.salvar(t));
    }

    /** Totem com consultas registradas nao e excluido (preserva indicadores): use status MANUTENCAO. */
    @Transactional
    public void excluir(Long id) {
        obter(id);
        if (consultaDAO.existePorTotem(id)) {
            throw new ConflitoException("O totem " + id + " possui consultas registradas. Use o status MANUTENCAO em vez de excluir.");
        }
        totemDAO.excluir(id);
    }

    Totem obter(Long id) {
        return totemDAO.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Totem " + id + " nao encontrado."));
    }

    private void garantirCodigoUnico(String codigo, Long idIgnorado) {
        if (totemDAO.existePorCodigo(codigo, idIgnorado)) {
            throw new ConflitoException("Ja existe um totem com o codigo " + codigo + ".");
        }
    }

    private static StatusTotem status(String status) {
        return Conversor.paraEnum(StatusTotem.class, status, "status");
    }
}
