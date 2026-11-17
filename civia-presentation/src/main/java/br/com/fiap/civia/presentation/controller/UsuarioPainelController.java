package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.UsuarioPainelBO;
import br.com.fiap.civia.application.dto.UsuarioPainelRequest;
import br.com.fiap.civia.application.dto.UsuarioPainelResponse;
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

/** Recurso /api/v1/usuarios (T_USUARIO_PAINEL). */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioPainelController {

    private final UsuarioPainelBO usuarioPainelBO;

    @GetMapping
    public ResponseEntity<List<UsuarioPainelResponse>> listar(@RequestParam(required = false) Long organizacaoId) {
        return ResponseEntity.ok(usuarioPainelBO.listar(organizacaoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPainelResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioPainelBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioPainelResponse> cadastrar(@Valid @RequestBody UsuarioPainelRequest request) {
        UsuarioPainelResponse criado = usuarioPainelBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioPainelResponse> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioPainelRequest request) {
        return ResponseEntity.ok(usuarioPainelBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioPainelBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
