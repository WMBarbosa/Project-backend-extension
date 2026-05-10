package com.barbosa.extension_project.infrastructure.integration;

import com.barbosa.extension_project.domain.enums.FormaPagamento;
import com.barbosa.extension_project.domain.enums.StatusPagamento;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GatewayPagamentoMock {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public record RespostaPagamento(
        StatusPagamento status,
        String transactionId,
        String payload,
        String motivoRecusa,
        LocalDateTime processadoEm
    ) {}

    public RespostaPagamento processar(Long pedidoId, BigDecimal valor, FormaPagamento forma) {
        log.info("[GATEWAY-MOCK] Iniciando processamento. pedidoId={}, valor={}, forma={}", pedidoId, valor, forma);

        // Simula latência de gateway real
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        String transactionId = "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime agora = LocalDateTime.now();

        // Regra de simulação: valor com centavos = .99 → recusado
        boolean recusado = valor.remainder(BigDecimal.ONE).compareTo(new BigDecimal("0.99")) == 0;

        if (recusado) {
            String payload = buildPayload(transactionId, pedidoId, valor, forma, "RECUSADO", "SALDO_INSUFICIENTE");
            log.warn("[GATEWAY-MOCK] Pagamento RECUSADO. pedidoId={}, transactionId={}", pedidoId, transactionId);
            return new RespostaPagamento(StatusPagamento.RECUSADO, transactionId, payload,
                "Saldo insuficiente ou cartão recusado (simulação)", agora);
        }

        String payload = buildPayload(transactionId, pedidoId, valor, forma, "APROVADO", null);
        log.info("[GATEWAY-MOCK] Pagamento APROVADO. pedidoId={}, transactionId={}", pedidoId, transactionId);
        return new RespostaPagamento(StatusPagamento.APROVADO, transactionId, payload, null, agora);
    }

    private String buildPayload(String txId, Long pedidoId, BigDecimal valor,
                                FormaPagamento forma, String resultado, String motivo) {
        try {
            Map<String, Object> data = new java.util.LinkedHashMap<>();
            data.put("gateway", "RaizesPayMock/v1");
            data.put("transactionId", txId);
            data.put("pedidoId", pedidoId);
            data.put("valor", valor);
            data.put("formaPagamento", forma.name());
            data.put("resultado", resultado);
            data.put("timestamp", LocalDateTime.now().toString());
            if (motivo != null) data.put("motivoRecusa", motivo);
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            return "{}";
        }
    }
}
