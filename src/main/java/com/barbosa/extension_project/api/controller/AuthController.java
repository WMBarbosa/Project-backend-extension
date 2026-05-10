package com.barbosa.extension_project.api.controller;

import com.barbosa.extension_project.application.dto.request.CadastroUsuarioRequest;
import com.barbosa.extension_project.application.dto.request.LoginRequest;
import com.barbosa.extension_project.application.dto.response.LoginResponse;
import com.barbosa.extension_project.application.dto.response.UsuarioResumoResponse;
import com.barbosa.extension_project.application.dto.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, cadastro de usuários e gerenciamento de sessão")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica o usuário e retorna um JWT Bearer token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastro de usuário",
            description = "Cadastra um novo usuário. Perfil padrão: CLIENTE")
    public ResponseEntity<UsuarioResumoResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(req));
    }
}
