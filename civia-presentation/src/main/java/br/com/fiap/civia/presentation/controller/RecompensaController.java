package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.RecompensaBO;
import br.com.fiap.civia.application.dto.RecompensaRequest;
import br.com.fiap.civia.application.dto.RecompensaResponse;
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

/** Recurso /api/v1/recompensas (T_RECOMPENSA). Filtro opcional: ?ativo=true|false. */
@RestController
@RequestMapping("/api/v1/recompensas")
@RequiredArgsConstructor
public class RecompensaController {

    private final RecompensaBO bo;

    @GetMapping
    public ResponseEntity<List<RecompensaResponse>> listar(@RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(bo.listar(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecompensaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<RecompensaResponse> cadastrar(@Valid @RequestBody RecompensaRequest request) {
        RecompensaResponse criado = bo.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecompensaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody RecompensaRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
