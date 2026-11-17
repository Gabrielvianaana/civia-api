package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.TrocaBO;
import br.com.fiap.civia.application.dto.TrocaRequest;
import br.com.fiap.civia.application.dto.TrocaResponse;
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

/** Recurso /api/v1/trocas (T_TROCA). Filtro opcional: ?cartaoId=. */
@RestController
@RequestMapping("/api/v1/trocas")
@RequiredArgsConstructor
public class TrocaController {

    private final TrocaBO bo;

    @GetMapping
    public ResponseEntity<List<TrocaResponse>> listar(@RequestParam(required = false) Long cartaoId) {
        return ResponseEntity.ok(bo.listar(cartaoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrocaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<TrocaResponse> cadastrar(@Valid @RequestBody TrocaRequest request) {
        TrocaResponse criado = bo.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrocaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody TrocaRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
