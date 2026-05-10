package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.request.MovimentoEstoqueRequest;
import com.barbosa.extension_project.application.dto.response.EstoqueResponse;
import com.barbosa.extension_project.application.dto.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque por unidade")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("/unidades/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Listar estoque de uma unidade")
    public ResponseEntity<List<EstoqueResponse>> listarPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.listarPorUnidade(unidadeId));
    }

    @GetMapping("/unidades/{unidadeId}/produtos/{produtoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Consultar saldo de produto em uma unidade")
    public ResponseEntity<EstoqueResponse> consultarSaldo(@PathVariable Long unidadeId,
                                                           @PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.consultarSaldo(unidadeId, produtoId));
    }

    @PostMapping("/unidades/{unidadeId}/movimentar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Movimentar estoque (entrada, saída, ajuste)")
    public ResponseEntity<EstoqueResponse> movimentar(@PathVariable Long unidadeId,
                                                       @Valid @RequestBody MovimentoEstoqueRequest req) {
        return ResponseEntity.ok(estoqueService.movimentar(unidadeId, req));
    }
}
