package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.MaterialBO;
import br.com.fiap.civia.application.dto.MaterialRequest;
import br.com.fiap.civia.application.dto.MaterialResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Recurso /api/v1/materiais (T_MATERIAL). */
@RestController
@RequestMapping("/api/v1/materiais")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialBO bo;

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> listar() {
        return ResponseEntity.ok(bo.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> cadastrar(@Valid @RequestBody MaterialRequest request) {
        MaterialResponse criado = bo.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> atualizar(@PathVariable Integer id, @Valid @RequestBody MaterialRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
