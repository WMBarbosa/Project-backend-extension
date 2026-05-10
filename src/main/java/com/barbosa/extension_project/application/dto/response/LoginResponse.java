package com.barbosa.extension_project.application.dto.response;

public record LoginResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    UsuarioResumoResponse usuario
) {
    public LoginResponse(String token, Long expiresIn, UsuarioResumoResponse usuario) {
        this(token, "Bearer", expiresIn, usuario);
    }
}
