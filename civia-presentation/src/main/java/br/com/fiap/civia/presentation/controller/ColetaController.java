package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.ColetaBO;
import br.com.fiap.civia.application.dto.ColetaRequest;
import br.com.fiap.civia.application.dto.ColetaResponse;
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

/** Recurso /api/v1/coletas (T_COLETA). Filtro opcional: ?postoId=. */
@RestController
@RequestMapping("/api/v1/coletas")
@RequiredArgsConstructor
public class ColetaController {

    private final ColetaBO bo;

    @GetMapping
    public ResponseEntity<List<ColetaResponse>> listar(@RequestParam(required = false) Long postoId) {
        return ResponseEntity.ok(bo.listar(postoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ColetaResponse> cadastrar(@Valid @RequestBody ColetaRequest request) {
        ColetaResponse criado = bo.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColetaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ColetaRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
