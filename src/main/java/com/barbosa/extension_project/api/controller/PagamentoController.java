package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.response.PagamentoResponse;
import com.barbosa.extension_project.application.dto.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Processamento de pagamentos via gateway mock")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping("/pedidos/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar pagamento de um pedido")
    public ResponseEntity<PagamentoResponse> consultarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.consultarPorPedido(pedidoId));
    }

    @PostMapping("/pedidos/{pedidoId}/processar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'ADMIN')")
    @Operation(summary = "Processar pagamento (mock)",
               description = """
                   Envia o pagamento ao gateway simulado e atualiza o status do pedido.
                   
                   **Regra de simulação:**
                   - Valores terminando em `.99` → RECUSADO (ex: R$ 50.99)
                   - Demais valores → APROVADO
                   
                   Quando APROVADO: status do pedido vai para PAGAMENTO_APROVADO.
                   Quando RECUSADO: status do pedido vai para CANCELADO.
                   """)
    public ResponseEntity<PagamentoResponse> processar(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(pedidoId));
    }
}
