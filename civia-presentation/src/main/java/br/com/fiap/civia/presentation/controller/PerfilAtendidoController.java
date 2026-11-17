package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.PerfilAtendidoBO;
import br.com.fiap.civia.application.dto.PerfilAtendidoRequest;
import br.com.fiap.civia.application.dto.PerfilAtendidoResponse;
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

/** Recurso /api/v1/perfis (T_PERFIL_ATENDIDO). */
@RestController
@RequestMapping("/api/v1/perfis")
@RequiredArgsConstructor
public class PerfilAtendidoController {

    private final PerfilAtendidoBO perfilAtendidoBO;

    @GetMapping
    public ResponseEntity<List<PerfilAtendidoResponse>> listar() {
        return ResponseEntity.ok(perfilAtendidoBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilAtendidoResponse> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(perfilAtendidoBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<PerfilAtendidoResponse> cadastrar(@Valid @RequestBody PerfilAtendidoRequest request) {
        PerfilAtendidoResponse criado = perfilAtendidoBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilAtendidoResponse> atualizar(@PathVariable Integer id, @Valid @RequestBody PerfilAtendidoRequest request) {
        return ResponseEntity.ok(perfilAtendidoBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        perfilAtendidoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
