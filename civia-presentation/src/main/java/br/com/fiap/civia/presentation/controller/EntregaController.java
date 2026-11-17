package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.EntregaBO;
import br.com.fiap.civia.application.dto.EntregaRequest;
import br.com.fiap.civia.application.dto.EntregaResponse;
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

/** Recurso /api/v1/entregas (T_ENTREGA). Filtros opcionais: ?cartaoId= e ?postoId=. */
@RestController
@RequestMapping("/api/v1/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaBO bo;

    @GetMapping
    public ResponseEntity<List<EntregaResponse>> listar(@RequestParam(required = false) Long cartaoId,
                                                         @RequestParam(required = false) Long postoId) {
        return ResponseEntity.ok(bo.listar(cartaoId, postoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<EntregaResponse> cadastrar(@Valid @RequestBody EntregaRequest request) {
        EntregaResponse criado = bo.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntregaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EntregaRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
