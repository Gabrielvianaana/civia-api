package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.PostoReciclagemBO;
import br.com.fiap.civia.application.dto.PostoReciclagemRequest;
import br.com.fiap.civia.application.dto.PostoReciclagemResponse;
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

/** Recurso /api/v1/postos-reciclagem (T_POSTO_RECICLAGEM). Filtro opcional: ?status=ATIVO|CHEIO|MANUTENCAO. */
@RestController
@RequestMapping("/api/v1/postos-reciclagem")
@RequiredArgsConstructor
public class PostoReciclagemController {

    private final PostoReciclagemBO bo;

    @GetMapping
    public ResponseEntity<List<PostoReciclagemResponse>> listar(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(bo.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostoReciclagemResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<PostoReciclagemResponse> cadastrar(@Valid @RequestBody PostoReciclagemRequest request) {
        PostoReciclagemResponse criado = bo.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostoReciclagemResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PostoReciclagemRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
