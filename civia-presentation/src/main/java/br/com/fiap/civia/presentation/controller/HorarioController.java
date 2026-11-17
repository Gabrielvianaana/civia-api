package br.com.fiap.civia.presentation.controller;

import br.com.fiap.civia.application.bo.HorarioBO;
import br.com.fiap.civia.application.dto.HorarioRequest;
import br.com.fiap.civia.application.dto.HorarioResponse;
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

/** Sub-recurso /api/v1/servicos/{servicoId}/horarios (T_HORARIO). */
@RestController
@RequestMapping("/api/v1/servicos/{servicoId}/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioBO horarioBO;

    @GetMapping
    public ResponseEntity<List<HorarioResponse>> listar(@PathVariable Long servicoId) {
        return ResponseEntity.ok(horarioBO.listar(servicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> buscar(@PathVariable Long servicoId, @PathVariable Long id) {
        return ResponseEntity.ok(horarioBO.buscar(servicoId, id));
    }

    @PostMapping
    public ResponseEntity<HorarioResponse> cadastrar(@PathVariable Long servicoId, @Valid @RequestBody HorarioRequest request) {
        HorarioResponse criado = horarioBO.cadastrar(servicoId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponse> atualizar(@PathVariable Long servicoId, @PathVariable Long id,
                                                     @Valid @RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioBO.atualizar(servicoId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long servicoId, @PathVariable Long id) {
        horarioBO.excluir(servicoId, id);
        return ResponseEntity.noContent().build();
    }
}
