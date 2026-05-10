package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.request.UnidadeRequest;
import com.barbosa.extension_project.application.dto.response.UnidadeResponse;
import com.barbosa.extension_project.application.dto.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gestão das unidades da rede Raízes do Nordeste")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    @Operation(summary = "Listar unidades ativas", description = "Público")
    public ResponseEntity<List<UnidadeResponse>> listarAtivas() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todas as unidades (incluindo inativas)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UnidadeResponse>> listarTodas() {
        return ResponseEntity.ok(unidadeService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<UnidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar unidade", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeService.criar(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Atualizar unidade", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UnidadeResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UnidadeRequest req) {
        return ResponseEntity.ok(unidadeService.atualizar(id, req));
    }
}
