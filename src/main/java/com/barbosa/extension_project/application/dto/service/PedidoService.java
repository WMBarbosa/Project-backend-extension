package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.request.AtualizarStatusPedidoRequest;
import com.barbosa.extension_project.application.dto.request.PedidoRequest;
import com.barbosa.extension_project.application.dto.response.PedidoResponse;
import com.barbosa.extension_project.domain.entity.*;
import com.barbosa.extension_project.domain.enums.CanalPedido;
import com.barbosa.extension_project.domain.enums.StatusPedido;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.domain.exception.TransicaoStatusInvalidaException;
import com.barbosa.extension_project.infrastructure.config.AuditService;
import com.barbosa.extension_project.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final EstoqueService estoqueService;
    private final FidelidadeService fidelidadeService;
    private final AuditService auditService;

    public Page<PedidoResponse> listar(CanalPedido canal, StatusPedido status,
                                       Long unidadeId, Pageable pageable) {
        return pedidoRepository.findWithFilters(canal, status, unidadeId, pageable)
            .map(PedidoResponse::of);
    }

    public PedidoResponse buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .map(PedidoResponse::of)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest req) {
        Usuario cliente = obterClienteAutenticado();

        Unidade unidade = unidadeRepository.findById(req.unidadeId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", req.unidadeId()));

        if (!unidade.getAtiva()) {
            throw new RegraDeNegocioException("Unidade " + req.unidadeId() + " está inativa.");
        }

        // Monta o pedido
        Pedido pedido = Pedido.builder()
            .cliente(cliente)
            .unidade(unidade)
            .canalPedido(req.canalPedido())
            .formaPagamento(req.formaPagamento())
            .observacao(req.observacao())
            .build();

        pedidoRepository.save(pedido);

        for (var itemReq : req.itens()) {
            Produto produto = produtoRepository.findById(itemReq.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", itemReq.produtoId()));

            if (!produto.getDisponivel()) {
                throw new RegraDeNegocioException("Produto '" + produto.getNome() + "' não está disponível.");
            }

            // Verifica e reserva estoque
            estoqueService.reservarEstoque(unidade.getId(), produto.getId(),
                itemReq.quantidade(), pedido.getId());

            ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(itemReq.quantidade())
                .precoUnitario(produto.getPreco())
                .observacao(itemReq.observacao())
                .build();
            item.calcularSubtotal();
            pedido.getItens().add(item);
        }

        BigDecimal desconto = BigDecimal.ZERO;
        int pontosUsados = 0;
        if (req.pontosParaResgatar() != null && req.pontosParaResgatar() > 0) {
            desconto = fidelidadeService.resgatarPontos(
                cliente.getId(), req.pontosParaResgatar(), pedido.getId());
            pontosUsados = req.pontosParaResgatar();
        }
        pedido.setDescontoFidelidade(desconto);
        pedido.setPontosUtilizados(pontosUsados);
        pedido.recalcularTotal();

        pedidoRepository.save(pedido);

        auditService.registrar("CRIAR_PEDIDO", "Pedido", pedido.getId(),
            "Canal: " + req.canalPedido() + " | Unidade: " + unidade.getNome() +
            " | Total: R$" + pedido.getValorTotal());

        return PedidoResponse.of(pedido);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long pedidoId, AtualizarStatusPedidoRequest req) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", pedidoId));

        if (!pedido.podeTransicionarPara(req.novoStatus())) {
            throw new TransicaoStatusInvalidaException(pedido.getStatus(), req.novoStatus());
        }

        StatusPedido statusAnterior = pedido.getStatus();
        pedido.setStatus(req.novoStatus());

        if (req.novoStatus() == StatusPedido.CANCELADO) {
            for (ItemPedido item : pedido.getItens()) {
                estoqueService.devolverEstoque(
                    pedido.getUnidade().getId(),
                    item.getProduto().getId(),
                    item.getQuantidade(),
                    pedidoId
                );
            }
        }

        if (req.novoStatus() == StatusPedido.ENTREGUE) {
            fidelidadeService.acumularPontosPorPedido(
                pedido.getCliente().getId(), pedido.getValorTotal(), pedidoId);
        }

        pedidoRepository.save(pedido);

        auditService.registrar("ATUALIZAR_STATUS_PEDIDO", "Pedido", pedidoId,
            statusAnterior + " → " + req.novoStatus());

        return PedidoResponse.of(pedido);
    }

    private Usuario obterClienteAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RegraDeNegocioException("Usuário autenticado não encontrado."));
    }
}
