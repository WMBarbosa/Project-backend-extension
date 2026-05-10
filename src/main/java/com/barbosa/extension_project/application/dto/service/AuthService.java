package com.barbosa.extension_project.application.dto.service;

import com.barbosa.extension_project.application.dto.request.CadastroUsuarioRequest;
import com.barbosa.extension_project.application.dto.request.LoginRequest;
import com.barbosa.extension_project.application.dto.response.LoginResponse;
import com.barbosa.extension_project.application.dto.response.UsuarioResumoResponse;
import com.barbosa.extension_project.domain.entity.SaldoFidelidade;
import com.barbosa.extension_project.domain.entity.Usuario;
import com.barbosa.extension_project.domain.enums.PerfilUsuario;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.infrastructure.config.AuditService;
import com.barbosa.extension_project.infrastructure.persistence.repository.SaldoFidelidadeRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.UsuarioRepository;
import com.barbosa.extension_project.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SaldoFidelidadeRepository saldoFidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Transactional
    public UsuarioResumoResponse cadastrar(CadastroUsuarioRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new RegraDeNegocioException("E-mail já cadastrado: " + req.email());
        }

        PerfilUsuario perfil = req.perfil() != null ? req.perfil() : PerfilUsuario.CLIENTE;

        Usuario usuario = Usuario.builder()
            .nome(req.nome())
            .email(req.email())
            .senha(passwordEncoder.encode(req.senha()))
            .telefone(req.telefone())
            .perfil(perfil)
            .ativo(true)
            .consentimentoFidelidade(Boolean.TRUE.equals(req.consentimentoFidelidade()))
            .dataConsentimento(Boolean.TRUE.equals(req.consentimentoFidelidade()) ? LocalDateTime.now() : null)
            .build();

        usuarioRepository.save(usuario);

        if (usuario.getConsentimentoFidelidade()) {
            SaldoFidelidade saldo = SaldoFidelidade.builder().usuario(usuario).build();
            saldoFidelidadeRepository.save(saldo);
        }

        auditService.registrar("CADASTRO_USUARIO", "Usuario", usuario.getId(),
            "Perfil: " + perfil + " | Consentimento fidelidade: " + usuario.getConsentimentoFidelidade());

        return UsuarioResumoResponse.of(usuario);
    }

    public LoginResponse login(LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.senha())
        );

        Usuario usuario = usuarioRepository.findByEmail(req.email())
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);

        auditService.registrar("LOGIN", "Usuario", usuario.getId(), "Login bem-sucedido");

        return new LoginResponse(token, jwtService.getExpiration() / 1000,
            UsuarioResumoResponse.of(usuario));
    }
}
