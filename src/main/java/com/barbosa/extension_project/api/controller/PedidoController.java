package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.request.AtualizarStatusPedidoRequest;
import com.barbosa.extension_project.application.dto.request.PedidoRequest;
import com.barbosa.extension_project.application.dto.response.PedidoResponse;
import com.barbosa.extension_project.application.dto.service.PedidoService;
import com.barbosa.extension_project.domain.enums.CanalPedido;
import com.barbosa.extension_project.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestão de pedidos — fluxo crítico do sistema")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'COZINHA')")
    @Operation(summary = "Listar pedidos",
               description = "Filtros disponíveis: canalPedido, status, unidadeId. Suporta paginação.")
    public ResponseEntity<Page<PedidoResponse>> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) Long unidadeId,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listar(canalPedido, status, unidadeId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'ADMIN')")
    @Operation(summary = "Criar pedido",
               description = "Cria um pedido validando estoque e calculando total. " +
                             "O campo canalPedido é obrigatório (APP, TOTEM, BALCAO, PICKUP, WEB).")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'COZINHA')")
    @Operation(summary = "Atualizar status do pedido",
               description = "Fluxo: AGUARDANDO_PAGAMENTO → PAGAMENTO_APROVADO → EM_PREPARO → PRONTO → ENTREGUE / CANCELADO")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AtualizarStatusPedidoRequest req) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, req));
    }
}
