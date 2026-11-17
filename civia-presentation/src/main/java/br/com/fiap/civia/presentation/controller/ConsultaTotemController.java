package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.ConsultaTotemBO;
import br.com.fiap.civia.application.dto.ConsultaTotemRequest;
import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
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

/** Recurso /api/v1/consultas (T_CONSULTA_TOTEM) e o registro de chegada pelo codigo impresso. */
@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
public class ConsultaTotemController {

    private final ConsultaTotemBO consultaTotemBO;

    @GetMapping
    public ResponseEntity<List<ConsultaTotemResponse>> listar(@RequestParam(required = false) Long totemId) {
        return ResponseEntity.ok(consultaTotemBO.listar(totemId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaTotemResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaTotemBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ConsultaTotemResponse> cadastrar(@Valid @RequestBody ConsultaTotemRequest request) {
        ConsultaTotemResponse criado = consultaTotemBO.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaTotemResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaTotemRequest request) {
        return ResponseEntity.ok(consultaTotemBO.atualizar(id, request));
    }

    /** US14: o servico informa o codigo impresso no papel e a chegada da pessoa e registrada. */
    @PutMapping("/chegada/{codigo}")
    public ResponseEntity<ConsultaTotemResponse> registrarChegada(@PathVariable String codigo) {
        return ResponseEntity.ok(consultaTotemBO.registrarChegada(codigo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        consultaTotemBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
