package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.SaldoFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaldoFidelidadeRepository extends JpaRepository<SaldoFidelidade, Long> {
    Optional<SaldoFidelidade> findByUsuarioId(Long usuarioId);
}
