package com.barbosa.extension_project.infrastructure.config;

import com.barbosa.extension_project.domain.entity.AuditLog;
import com.barbosa.extension_project.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void registrar(String acao, String recurso, Long recursoId, String detalhes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth != null ? auth.getName() : "anonimo";
            String perfil = auth != null && !auth.getAuthorities().isEmpty()
                    ? auth.getAuthorities().iterator().next().getAuthority() : "UNKNOWN";

            AuditLog entry = AuditLog.builder()
                .usuarioEmail(email)
                .usuarioPerfil(perfil)
                .acao(acao)
                .recurso(recurso)
                .recursoId(recursoId)
                .detalhes(detalhes)
                .build();

            auditLogRepository.save(entry);
            log.info("[AUDIT] {} | {} | {} | id={} | {}", email, acao, recurso, recursoId, detalhes);
        } catch (Exception e) {
            log.error("Falha ao registrar audit log: {}", e.getMessage());
        }
    }
}
