package com.barbosa.extension_project.application.dto.request;

import com.barbosa.extension_project.domain.enums.CanalPedido;
import com.barbosa.extension_project.domain.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
    @NotNull(message = "Canal do pedido é obrigatório")
    CanalPedido canalPedido,

    @NotNull(message = "Unidade é obrigatória")
    Long unidadeId,

    @NotEmpty(message = "O pedido deve ter ao menos um item")
    @Valid
    List<ItemPedidoRequest> itens,

    @NotNull(message = "Forma de pagamento é obrigatória")
    FormaPagamento formaPagamento,

    Integer pontosParaResgatar,

    String observacao
) {}
