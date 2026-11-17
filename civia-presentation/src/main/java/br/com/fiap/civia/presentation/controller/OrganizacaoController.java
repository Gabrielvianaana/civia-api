package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.OrganizacaoBO;
import br.com.fiap.civia.application.dto.OrganizacaoRequest;
import br.com.fiap.civia.application.dto.OrganizacaoResponse;
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

/** Recurso /api/v1/organizacoes (T_ORGANIZACAO). */
@RestController
@RequestMapping("/api/v1/organizacoes")
@RequiredArgsConstructor
public class OrganizacaoController {

    private final OrganizacaoBO organizacaoBO;

    @GetMapping
    public ResponseEntity<List<OrganizacaoResponse>> listar(@RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(organizacaoBO.listar(tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(organizacaoBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<OrganizacaoResponse> cadastrar(@Valid @RequestBody OrganizacaoRequest request) {
        OrganizacaoResponse criado = organizacaoBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizacaoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody OrganizacaoRequest request) {
        return ResponseEntity.ok(organizacaoBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        organizacaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
