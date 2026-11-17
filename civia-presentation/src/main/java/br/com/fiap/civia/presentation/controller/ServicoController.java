package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.ServicoBO;
import br.com.fiap.civia.application.dto.ServicoRequest;
import br.com.fiap.civia.application.dto.ServicoResponse;
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

/** Recurso /api/v1/servicos: cadastro de servicos parceiros (abrigo, refeicao, banho, atendimento). */
@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoBO servicoBO;

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar(@RequestParam(required = false) String tipo,
                                                        @RequestParam(required = false) Boolean aprovado) {
        return ResponseEntity.ok(servicoBO.listar(tipo, aprovado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicoBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> cadastrar(@Valid @RequestBody ServicoRequest request) {
        ServicoResponse criado = servicoBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(servicoBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        servicoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
