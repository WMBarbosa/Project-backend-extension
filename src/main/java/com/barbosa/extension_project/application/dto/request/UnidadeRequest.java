package com.barbosa.extension_project.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadeRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Size(max = 300) String endereco,
    @Size(max = 20) String telefone,
    @Size(max = 150) String cidade,
    @Size(max = 2) String estado,
    @Size(max = 20) String cnpj,
    Boolean ativa
) {}
