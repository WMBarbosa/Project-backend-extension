package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.MovimentoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long> {
    Page<MovimentoEstoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId, Pageable pageable);
    Page<MovimentoEstoque> findByUnidadeId(Long unidadeId, Pageable pageable);
}
