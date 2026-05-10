package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.request.MovimentoEstoqueRequest;
import com.barbosa.extension_project.application.dto.response.EstoqueResponse;
import com.barbosa.extension_project.domain.entity.EstoqueUnidade;
import com.barbosa.extension_project.domain.entity.MovimentoEstoque;
import com.barbosa.extension_project.domain.entity.Produto;
import com.barbosa.extension_project.domain.entity.Unidade;
import com.barbosa.extension_project.domain.enums.TipoMovimentoEstoque;
import com.barbosa.extension_project.domain.exception.EstoqueInsuficienteException;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.infrastructure.config.AuditService;
import com.barbosa.extension_project.infrastructure.persistence.repository.EstoqueUnidadeRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.MovimentoEstoqueRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.ProdutoRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueUnidadeRepository estoqueRepository;
    private final MovimentoEstoqueRepository movimentoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditService auditService;

    public List<EstoqueResponse> listarPorUnidade(Long unidadeId) {
        return estoqueRepository.findByUnidadeId(unidadeId)
            .stream().map(EstoqueResponse::of).toList();
    }

    public EstoqueResponse consultarSaldo(Long unidadeId, Long produtoId) {
        return estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
            .map(EstoqueResponse::of)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Estoque não encontrado para unidade=" + unidadeId + " produto=" + produtoId));
    }

    @Transactional
    public EstoqueResponse movimentar(Long unidadeId, MovimentoEstoqueRequest req) {
        Unidade unidade = unidadeRepository.findById(unidadeId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade", unidadeId));

        Produto produto = produtoRepository.findById(req.produtoId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", req.produtoId()));

        EstoqueUnidade estoque = estoqueRepository
            .findByUnidadeIdAndProdutoId(unidadeId, req.produtoId())
            .orElseGet(() -> EstoqueUnidade.builder()
                .unidade(unidade).produto(produto).quantidade(0).build());

        int qtdAntes = estoque.getQuantidade();

        if (req.tipo() == TipoMovimentoEstoque.ENTRADA || req.tipo() == TipoMovimentoEstoque.AJUSTE) {
            estoque.incrementar(req.quantidade());
        } else if (req.tipo() == TipoMovimentoEstoque.SAIDA) {
            if (!estoque.temSaldo(req.quantidade())) {
                throw new EstoqueInsuficienteException(produto.getId(), estoque.getQuantidade(), req.quantidade());
            }
            estoque.reduzir(req.quantidade());
        }

        estoqueRepository.save(estoque);

        MovimentoEstoque movimento = MovimentoEstoque.builder()
            .unidade(unidade).produto(produto)
            .tipo(req.tipo())
            .quantidade(req.quantidade())
            .quantidadeAnterior(qtdAntes)
            .quantidadePosterior(estoque.getQuantidade())
            .observacao(req.observacao())
            .build();
        movimentoRepository.save(movimento);

        auditService.registrar("MOVIMENTACAO_ESTOQUE", "EstoqueUnidade", estoque.getId(),
            req.tipo() + " | qtd=" + req.quantidade() + " | unidade=" + unidadeId + " | produto=" + produto.getNome());

        return EstoqueResponse.of(estoque);
    }

    @Transactional
    public void reservarEstoque(Long unidadeId, Long produtoId, int quantidade, Long pedidoId) {
        EstoqueUnidade estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Estoque não encontrado para produto=" + produtoId + " na unidade=" + unidadeId));

        if (!estoque.temSaldo(quantidade)) {
            throw new EstoqueInsuficienteException(produtoId, estoque.getQuantidade(), quantidade);
        }

        int antes = estoque.getQuantidade();
        estoque.reduzir(quantidade);
        estoqueRepository.save(estoque);

        MovimentoEstoque mov = MovimentoEstoque.builder()
            .unidade(estoque.getUnidade()).produto(estoque.getProduto())
            .tipo(TipoMovimentoEstoque.RESERVA)
            .quantidade(quantidade)
            .quantidadeAnterior(antes)
            .quantidadePosterior(estoque.getQuantidade())
            .pedidoId(pedidoId)
            .observacao("Reserva para pedido #" + pedidoId)
            .build();
        movimentoRepository.save(mov);
    }

    @Transactional
    public void devolverEstoque(Long unidadeId, Long produtoId, int quantidade, Long pedidoId) {
        EstoqueUnidade estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Estoque não encontrado para produto=" + produtoId));

        int antes = estoque.getQuantidade();
        estoque.incrementar(quantidade);
        estoqueRepository.save(estoque);

        MovimentoEstoque mov = MovimentoEstoque.builder()
            .unidade(estoque.getUnidade()).produto(estoque.getProduto())
            .tipo(TipoMovimentoEstoque.CANCELAMENTO)
            .quantidade(quantidade)
            .quantidadeAnterior(antes)
            .quantidadePosterior(estoque.getQuantidade())
            .pedidoId(pedidoId)
            .observacao("Devolução por cancelamento do pedido #" + pedidoId)
            .build();
        movimentoRepository.save(mov);
    }
}
