package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.SaldoFidelidade;

public record FidelidadeResponse(
    Long usuarioId, String usuarioNome, int pontosAcumulados,
    int pontosResgatados, int saldoDisponivel
) {
    public static FidelidadeResponse of(SaldoFidelidade s) {
        return new FidelidadeResponse(
            s.getUsuario().getId(), s.getUsuario().getNome(),
            s.getPontosAcumulados(), s.getPontosResgatados(), s.getSaldoDisponivel()
        );
    }
}
