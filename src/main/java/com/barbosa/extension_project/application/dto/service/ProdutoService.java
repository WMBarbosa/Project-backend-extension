package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.request.ProdutoRequest;
import com.barbosa.extension_project.application.dto.response.ProdutoResponse;
import com.barbosa.extension_project.domain.entity.Produto;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.infrastructure.persistence.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Page<ProdutoResponse> listar(Boolean disponivel, String nome, Pageable pageable) {
        if (nome != null && !nome.isBlank()) {
            return produtoRepository.findByNomeContaining(nome, pageable).map(ProdutoResponse::of);
        }
        if (disponivel != null) {
            return produtoRepository.findByDisponivel(disponivel, pageable).map(ProdutoResponse::of);
        }
        return produtoRepository.findAll(pageable).map(ProdutoResponse::of);
    }

    public ProdutoResponse buscarPorId(Long id) {
        return produtoRepository.findById(id)
            .map(ProdutoResponse::of)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        Produto produto = Produto.builder()
            .nome(req.nome())
            .descricao(req.descricao())
            .preco(req.preco())
            .categoria(req.categoria())
            .imagemUrl(req.imagemUrl())
            .disponivel(req.disponivel() != null ? req.disponivel() : true)
            .build();
        return ProdutoResponse.of(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        produto.setNome(req.nome());
        produto.setDescricao(req.descricao());
        produto.setPreco(req.preco());
        produto.setCategoria(req.categoria());
        produto.setImagemUrl(req.imagemUrl());
        if (req.disponivel() != null) produto.setDisponivel(req.disponivel());

        return ProdutoResponse.of(produtoRepository.save(produto));
    }

    @Transactional
    public void excluir(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto", id);
        }
        produtoRepository.deleteById(id);
    }
}
