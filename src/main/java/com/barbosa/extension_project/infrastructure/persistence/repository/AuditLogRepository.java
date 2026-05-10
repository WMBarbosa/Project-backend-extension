package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
