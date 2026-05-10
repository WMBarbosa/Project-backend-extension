package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.HistoricoFidelidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoFidelidadeRepository extends JpaRepository<HistoricoFidelidade, Long> {
    Page<HistoricoFidelidade> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);
}
