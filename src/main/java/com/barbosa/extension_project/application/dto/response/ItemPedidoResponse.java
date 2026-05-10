package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.ItemPedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
    Long id,
    Long produtoId,
    String produtoNome,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal,
    String observacao
) {
    public static ItemPedidoResponse of(ItemPedido item) {
        return new ItemPedidoResponse(
            item.getId(),
            item.getProduto().getId(),
            item.getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(),
            item.getSubtotal(),
            item.getObservacao()
        );
    }
}
