package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.EstoqueUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EstoqueUnidadeRepository extends JpaRepository<EstoqueUnidade, Long> {

    Optional<EstoqueUnidade> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);

    List<EstoqueUnidade> findByUnidadeId(Long unidadeId);

    @Query("SELECT e FROM EstoqueUnidade e WHERE e.unidade.id = :unidadeId AND e.quantidade <= e.quantidadeMinima")
    List<EstoqueUnidade> findEstoqueCriticoByUnidade(Long unidadeId);
}
