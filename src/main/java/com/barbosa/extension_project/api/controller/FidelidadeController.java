package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.response.FidelidadeResponse;
import com.barbosa.extension_project.application.dto.response.HistoricoFidelidadeResponse;
import com.barbosa.extension_project.application.dto.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de fidelização — acúmulo e resgate de pontos (requer consentimento LGPD)")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @GetMapping("/usuarios/{usuarioId}/saldo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    @Operation(summary = "Consultar saldo de pontos do usuário")
    public ResponseEntity<FidelidadeResponse> consultarSaldo(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(fidelidadeService.consultarSaldo(usuarioId));
    }

    @GetMapping("/usuarios/{usuarioId}/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    @Operation(summary = "Histórico de pontos do usuário")
    public ResponseEntity<Page<HistoricoFidelidadeResponse>> historico(
            @PathVariable Long usuarioId, Pageable pageable) {
        return ResponseEntity.ok(fidelidadeService.historico(usuarioId, pageable));
    }

    @PostMapping("/usuarios/{usuarioId}/consentimento")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Ativar consentimento para programa de fidelidade (LGPD)",
               description = "Registra o consentimento do usuário conforme LGPD e ativa o programa de pontos.")
    public ResponseEntity<FidelidadeResponse> ativarConsentimento(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(fidelidadeService.ativarConsentimento(usuarioId));
    }
}
