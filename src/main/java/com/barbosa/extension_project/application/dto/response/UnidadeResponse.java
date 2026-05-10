package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.Unidade;

import java.time.LocalDateTime;

public record UnidadeResponse(
    Long id, String nome, String endereco, String telefone,
    String cidade, String estado, String cnpj, Boolean ativa, LocalDateTime criadoEm
) {
    public static UnidadeResponse of(Unidade u) {
        return new UnidadeResponse(u.getId(), u.getNome(), u.getEndereco(), u.getTelefone(),
            u.getCidade(), u.getEstado(), u.getCnpj(), u.getAtiva(), u.getCriadoEm());
    }
}
