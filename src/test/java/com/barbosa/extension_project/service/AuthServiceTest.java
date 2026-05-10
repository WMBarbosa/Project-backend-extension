package com.barbosa.extension_project.service;

import com.barbosa.extension_project.application.dto.request.CadastroUsuarioRequest;
import com.barbosa.extension_project.application.dto.request.LoginRequest;
import com.barbosa.extension_project.application.dto.response.LoginResponse;
import com.barbosa.extension_project.application.dto.response.UsuarioResumoResponse;
import com.barbosa.extension_project.application.dto.service.AuthService;
import com.barbosa.extension_project.domain.entity.Usuario;
import com.barbosa.extension_project.domain.enums.PerfilUsuario;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.infrastructure.config.AuditService;
import com.barbosa.extension_project.infrastructure.persistence.repository.SaldoFidelidadeRepository;
import com.barbosa.extension_project.infrastructure.persistence.repository.UsuarioRepository;
import com.barbosa.extension_project.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("T01-T04 — AuthService")
class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    SaldoFidelidadeRepository saldoFidelidadeRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock AuthenticationManager authenticationManager;

    @Mock
    AuditService auditService;

    @InjectMocks
    AuthService authService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setup() {
        usuarioExistente = Usuario.builder()
            .id(1L).nome("Cliente Teste").email("cliente@raizesnordeste.com")
            .senha("$2a$hashed").perfil(PerfilUsuario.CLIENTE).ativo(true)
            .consentimentoFidelidade(false).build();
    }

    @Test
    @DisplayName("T01 — Login válido deve retornar token")
    void t01_loginValido_retornaToken() {
        when(usuarioRepository.findByEmail("cliente@raizesnordeste.com"))
            .thenReturn(Optional.of(usuarioExistente));
        when(jwtService.gerarToken(any())).thenReturn("jwt-token-mock");
        when(jwtService.getExpiration()).thenReturn(86400000L);

        LoginResponse resp = authService.login(
            new LoginRequest("cliente@raizesnordeste.com", "Cliente@123"));

        assertThat(resp.accessToken()).isEqualTo("jwt-token-mock");
        assertThat(resp.tokenType()).isEqualTo("Bearer");
        assertThat(resp.usuario().email()).isEqualTo("cliente@raizesnordeste.com");
    }

    @Test
    @DisplayName("T02 — Cadastro com e-mail duplicado deve lançar exceção")
    void t02_cadastroEmailDuplicado_lancaExcecao() {
        when(usuarioRepository.existsByEmail("cliente@raizesnordeste.com")).thenReturn(true);

        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "Novo Cliente", "cliente@raizesnordeste.com", "Senha@123",
            null, PerfilUsuario.CLIENTE, false);

        assertThatThrownBy(() -> authService.cadastrar(req))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    @DisplayName("T03 — Cadastro com consentimento deve criar saldo fidelidade")
    void t03_cadastroComConsentimento_criaSaldoFidelidade() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
        when(usuarioRepository.save(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "Maria Silva", "maria@teste.com", "Senha@123",
            "(81) 99999-0000", PerfilUsuario.CLIENTE, true);

        UsuarioResumoResponse resp = authService.cadastrar(req);

        assertThat(resp.nome()).isEqualTo("Maria Silva");
        verify(saldoFidelidadeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("T04 — Cadastro sem consentimento não deve criar saldo fidelidade")
    void t04_cadastroSemConsentimento_naoDeveCriarSaldo() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
        when(usuarioRepository.save(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(11L);
            return u;
        });

        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "João Costa", "joao@teste.com", "Senha@123",
            null, PerfilUsuario.CLIENTE, false);

        authService.cadastrar(req);

        verify(saldoFidelidadeRepository, never()).save(any());
    }
}
