package com.barbosa.extension_project.application.dto.request;

import com.barbosa.extension_project.domain.enums.TipoMovimentoEstoque;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovimentoEstoqueRequest(
    @NotNull Long produtoId,
    @NotNull TipoMovimentoEstoque tipo,
    @NotNull @Min(1) Integer quantidade,
    String observacao
) {}
