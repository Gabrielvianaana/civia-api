package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.CartaoBO;
import br.com.fiap.civia.application.dto.CartaoAtualizacaoRequest;
import br.com.fiap.civia.application.dto.CartaoRequest;
import br.com.fiap.civia.application.dto.CartaoResponse;
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

/** Recurso /api/v1/cartoes (T_CARTAO): emissao, substituicao (RN12) e consulta de saldo pelo codigo. */
@RestController
@RequestMapping("/api/v1/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoBO bo;

    @GetMapping
    public ResponseEntity<List<CartaoResponse>> listar(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(bo.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartaoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    /** Consulta do saldo pelo codigo impresso no cartao (sem dado pessoal - RN07). */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CartaoResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(bo.buscarPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<CartaoResponse> cadastrar(@Valid @RequestBody CartaoRequest request) {
        CartaoResponse criado = bo.emitir(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartaoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CartaoAtualizacaoRequest request) {
        return ResponseEntity.ok(bo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
