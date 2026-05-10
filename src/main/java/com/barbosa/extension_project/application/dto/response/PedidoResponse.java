package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.Pedido;
import com.barbosa.extension_project.domain.enums.CanalPedido;
import com.barbosa.extension_project.domain.enums.FormaPagamento;
import com.barbosa.extension_project.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
    Long id,
    Long clienteId,
    String clienteNome,
    Long unidadeId,
    String unidadeNome,
    CanalPedido canalPedido,
    StatusPedido status,
    FormaPagamento formaPagamento,
    BigDecimal valorTotal,
    BigDecimal descontoFidelidade,
    Integer pontosUtilizados,
    List<ItemPedidoResponse> itens,
    String observacao,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public static PedidoResponse of(Pedido p) {
        return new PedidoResponse(
            p.getId(),
            p.getCliente().getId(),
            p.getCliente().getNome(),
            p.getUnidade().getId(),
            p.getUnidade().getNome(),
            p.getCanalPedido(),
            p.getStatus(),
            p.getFormaPagamento(),
            p.getValorTotal(),
            p.getDescontoFidelidade(),
            p.getPontosUtilizados(),
            p.getItens().stream().map(ItemPedidoResponse::of).toList(),
            p.getObservacao(),
            p.getCriadoEm(),
            p.getAtualizadoEm()
        );
    }
}
