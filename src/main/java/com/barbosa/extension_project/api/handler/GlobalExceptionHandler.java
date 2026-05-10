package com.barbosa.extension_project.api.handler;

import com.barbosa.extension_project.domain.exception.EstoqueInsuficienteException;
import com.barbosa.extension_project.domain.exception.RecursoNaoEncontradoException;
import com.barbosa.extension_project.domain.exception.RegraDeNegocioException;
import com.barbosa.extension_project.domain.exception.TransicaoStatusInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
        String error, String message, List<Map<String, String>> details,
        String timestamp, String path
    ) {}

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("RECURSO_NAO_ENCONTRADO", ex.getMessage(),
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ApiError> handleBusinessRule(RegraDeNegocioException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("REGRA_DE_NEGOCIO", ex.getMessage(),
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ApiError> handleTransicao(TransicaoStatusInvalidaException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("TRANSICAO_INVALIDA", ex.getMessage(),
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ApiError> handleEstoque(EstoqueInsuficienteException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("ESTOQUE_INSUFICIENTE", ex.getMessage(),
                List.of(Map.of("field", "itens[].quantidade",
                    "issue", "Disponível: " + ex.getDisponivel())),
                LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
            .map((FieldError fe) -> Map.of("field", fe.getField(), "issue", fe.getDefaultMessage()))
            .toList();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ApiError("VALIDACAO_INVALIDA", "Erro de validação nos campos informados.",
                details, LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiError("ACESSO_NEGADO", "Você não tem permissão para realizar esta ação.",
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleUnauthorized(AuthenticationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiError("NAO_AUTENTICADO", "Token inválido ou ausente.",
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiError("ERRO_INTERNO", "Erro interno no servidor. Tente novamente.",
                List.of(), LocalDateTime.now().toString(), req.getRequestURI()));
    }
}
