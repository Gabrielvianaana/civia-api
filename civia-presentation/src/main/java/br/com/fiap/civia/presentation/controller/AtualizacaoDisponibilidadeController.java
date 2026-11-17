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

/** Recurso /api/v1/atualizacoes (T_ATUALIZACAO): disponibilidade informada pelo painel ou pela Central de Vagas. */
@RestController
@RequestMapping("/api/v1/atualizacoes")
@RequiredArgsConstructor
public class AtualizacaoDisponibilidadeController {

    private final AtualizacaoDisponibilidadeBO atualizacaoDisponibilidadeBO;

    @GetMapping
    public ResponseEntity<List<AtualizacaoDisponibilidadeResponse>> listar(@RequestParam(required = false) Long servicoId) {
        return ResponseEntity.ok(atualizacaoDisponibilidadeBO.listar(servicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(atualizacaoDisponibilidadeBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> cadastrar(@Valid @RequestBody AtualizacaoDisponibilidadeRequest request) {
        AtualizacaoDisponibilidadeResponse criado = atualizacaoDisponibilidadeBO.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtualizacaoDisponibilidadeResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizacaoDisponibilidadeRequest request) {
        return ResponseEntity.ok(atualizacaoDisponibilidadeBO.corrigir(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atualizacaoDisponibilidadeBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
