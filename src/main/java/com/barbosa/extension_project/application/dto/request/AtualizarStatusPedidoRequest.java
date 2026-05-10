package com.barbosa.extension_project.application.dto.request;

import com.barbosa.extension_project.domain.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoRequest(
    @NotNull(message = "Novo status é obrigatório")
    StatusPedido novoStatus,
    String observacao
) {}
