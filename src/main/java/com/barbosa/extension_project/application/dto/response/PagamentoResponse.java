package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.enums.FormaPagamento;
import com.barbosa.extension_project.domain.enums.StatusPagamento;
import com.barbosa.extension_project.domain.enums.entity.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
    Long id,
    Long pedidoId,
    FormaPagamento formaPagamento,
    StatusPagamento status,
    BigDecimal valor,
    String gatewayTransactionId,
    String motivoRecusa,
    LocalDateTime processadoEm,
    LocalDateTime criadoEm
) {
    public static PagamentoResponse of(Pagamento p) {
        return new PagamentoResponse(
            p.getId(),
            p.getPedido().getId(),
            p.getFormaPagamento(),
            p.getStatus(),
            p.getValor(),
            p.getGatewayTransactionId(),
            p.getMotivoRecusa(),
            p.getProcessadoEm(),
            p.getCriadoEm()
        );
    }
}
