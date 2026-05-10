package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.EstoqueUnidade;

public record EstoqueResponse(
    Long id, Long unidadeId, String unidadeNome,
    Long produtoId, String produtoNome,
    Integer quantidade, Integer quantidadeMinima
) {
    public static EstoqueResponse of(EstoqueUnidade e) {
        return new EstoqueResponse(
            e.getId(), e.getUnidade().getId(), e.getUnidade().getNome(),
            e.getProduto().getId(), e.getProduto().getNome(),
            e.getQuantidade(), e.getQuantidadeMinima()
        );
    }
}
