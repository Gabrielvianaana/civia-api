package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.AtualizacaoDisponibilidadeBO;
import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeRequest;
import br.com.fiap.civia.application.dto.AtualizacaoDisponibilidadeResponse;
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

/** Recurso /api/v1/atualizacoes: registro da disponibilidade dos servicos pelas equipes (painel). */
@RestController
@RequestMapping("/api/v1/atualizacoes")
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeController {

    private final AtualizacaoDisponibilidadeBO atualizacaoBO;

    @GetMapping
    public ResponseEntity<List<AtualizacaoDisponibilidadeResponse>> listar(
            @RequestParam(required = false) Long servicoId) {
        return ResponseEntity.ok(atualizacaoBO.listar(servicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(atualizacaoBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> registrar(
            @Valid @RequestBody AtualizacaoDisponibilidadeRequest request) {
        AtualizacaoDisponibilidadeResponse criada = atualizacaoBO.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> corrigir(
            @PathVariable Long id, @Valid @RequestBody AtualizacaoDisponibilidadeRequest request) {
        return ResponseEntity.ok(atualizacaoBO.corrigir(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atualizacaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
