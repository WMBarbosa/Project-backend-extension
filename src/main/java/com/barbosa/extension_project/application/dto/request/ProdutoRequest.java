package com.barbosa.extension_project.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 150)
    String nome,

    @Size(max = 500)
    String descricao,

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal preco,

    @Size(max = 100)
    String categoria,

    String imagemUrl,
    Boolean disponivel
) {}
