package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.response.PagamentoResponse;
import com.barbosa.extension_project.domain.entity.Pagamento;
import com.barbosa.extension_project.domain.entity.Pedido;
import com.barbosa.extension_project.domain.enums.StatusPagamento;
import com.barbosa.extension_project.domain.enums.StatusPedido;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.infrastructure.config.AuditService;
import com.barbosa.extension_project.infrastructure.integration.GatewayPagamentoMock;
import com.barbosa.extension_project.infrastructure.persistence.repository.PagamentoRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final GatewayPagamentoMock gatewayMock;
    private final AuditService auditService;

    public PagamentoResponse consultarPorPedido(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId)
            .map(PagamentoResponse::of)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Pagamento não encontrado para o pedido " + pedidoId));
    }

    @Transactional
    public PagamentoResponse processarPagamento(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));

        pagamentoRepository.findByPedidoId(pedidoId).ifPresent(pag -> {
            if (pag.getStatus() == StatusPagamento.APROVADO) {
                throw new RegraDeNegocioException(
                    "Pagamento já aprovado para o pedido " + pedidoId);
            }
        });

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraDeNegocioException(
                "Pedido não está aguardando pagamento. Status atual: " + pedido.getStatus());
        }

        GatewayPagamentoMock.RespostaPagamento resposta = gatewayMock.processar(
            pedidoId, pedido.getValorTotal(), pedido.getFormaPagamento());

        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
            .orElse(Pagamento.builder()
                .pedido(pedido)
                .formaPagamento(pedido.getFormaPagamento())
                .valor(pedido.getValorTotal())
                .build());

        pagamento.setStatus(resposta.status());
        pagamento.setGatewayTransactionId(resposta.transactionId());
        pagamento.setGatewayResponse(resposta.payload());
        pagamento.setMotivoRecusa(resposta.motivoRecusa());
        pagamento.setProcessadoEm(resposta.processadoEm());
        pagamentoRepository.save(pagamento);

        if (resposta.status() == StatusPagamento.APROVADO) {
            pedido.setStatus(StatusPedido.PAGAMENTO_APROVADO);
        } else {
            pedido.setStatus(StatusPedido.CANCELADO);
        }
        pedidoRepository.save(pedido);

        auditService.registrar("PROCESSAR_PAGAMENTO", "Pagamento", pagamento.getId(),
            "Pedido #" + pedidoId + " | Status: " + resposta.status() +
            " | TxId: " + resposta.transactionId());

        return PagamentoResponse.of(pagamento);
    }
}
