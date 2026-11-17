package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.ConsultaTotemBO;
import br.com.fiap.civia.application.bo.TotemBO;
import br.com.fiap.civia.application.dto.ConsultaTotemResponse;
import br.com.fiap.civia.application.dto.TotemRequest;
import br.com.fiap.civia.application.dto.TotemResponse;
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

/**
 * Recurso /api/v1/totens e o sub-recurso /servicos (consulta feita pelo totem).
 */
@RestController
@RequestMapping("/api/v1/totens")
@RequiredArgsConstructor
public class TotemController {

    private final TotemBO totemBO;
    private final ConsultaTotemBO consultaTotemBO;

    @GetMapping
    public ResponseEntity<List<TotemResponse>> listar() {
        return ResponseEntity.ok(totemBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TotemResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(totemBO.buscar(id));
    }

    /** Ate 3 servicos mais proximos do totem, com o status de disponibilidade de cada um. */
    @GetMapping("/{id}/servicos")
    public ResponseEntity<ConsultaTotemResponse> consultarServicos(@PathVariable Long id,
                                                                   @RequestParam(required = false) String tipo,
                                                                   @RequestParam(required = false) String perfil) {
        return ResponseEntity.ok(consultaTotemBO.consultar(id, tipo, perfil));
    }

    @PostMapping
    public ResponseEntity<TotemResponse> cadastrar(@Valid @RequestBody TotemRequest request) {
        TotemResponse criado = totemBO.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TotemResponse> atualizar(@PathVariable Long id, @Valid @RequestBody TotemRequest request) {
        return ResponseEntity.ok(totemBO.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        totemBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
