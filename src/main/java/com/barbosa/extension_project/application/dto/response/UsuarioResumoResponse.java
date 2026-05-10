package com.barbosa.extension_project.application.dto.response;

import com.barbosa.extension_project.domain.entity.Usuario;
import com.barbosa.extension_project.domain.enums.PerfilUsuario;

public record UsuarioResumoResponse(Long id, String nome, String email, PerfilUsuario perfil) {
    public static UsuarioResumoResponse of(Usuario u) {
        return new UsuarioResumoResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }
}
