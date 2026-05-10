package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.HistoricoFidelidade;

import java.time.LocalDateTime;

public record HistoricoFidelidadeResponse(
    Long id, int pontos, String tipo, String descricao,
    Long pedidoId, LocalDateTime criadoEm
) {
    public static HistoricoFidelidadeResponse of(HistoricoFidelidade h) {
        return new HistoricoFidelidadeResponse(
            h.getId(), h.getPontos(), h.getTipo(),
            h.getDescricao(), h.getPedidoId(), h.getCriadoEm()
        );
    }
}
