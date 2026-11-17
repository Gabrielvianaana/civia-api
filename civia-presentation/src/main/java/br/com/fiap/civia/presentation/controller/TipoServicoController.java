package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.TipoServicoBO;
import br.com.fiap.civia.application.dto.TipoServicoRequest;
import br.com.fiap.civia.application.dto.TipoServicoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Recurso /api/v1/tipos-servico (T_TIPO_SERVICO): opcoes da tela inicial do totem. */
@RestController
@RequestMapping("/api/v1/tipos-servico")
@RequiredArgsConstructor
public class TipoServicoController {

    private final TipoServicoBO tipoServicoBO;

    @GetMapping
    public ResponseEntity<List<TipoServicoResponse>> listar() {
        return ResponseEntity.ok(tipoServicoBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicoResponse> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoServicoBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<TipoServicoResponse> cadastrar(@Valid @RequestBody TipoServicoRequest request) {
        TipoServicoResponse criado = tipoServicoBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicoResponse> atualizar(@PathVariable Integer id, @Valid @RequestBody TipoServicoRequest request) {
        return ResponseEntity.ok(tipoServicoBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        tipoServicoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
