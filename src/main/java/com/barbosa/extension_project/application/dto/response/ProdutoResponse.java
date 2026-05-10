package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
    Long id, String nome, String descricao, BigDecimal preco,
    String categoria, String imagemUrl, Boolean disponivel, LocalDateTime criadoEm
) {
    public static ProdutoResponse of(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getDescricao(),
            p.getPreco(), p.getCategoria(), p.getImagemUrl(), p.getDisponivel(), p.getCriadoEm());
    }
}
