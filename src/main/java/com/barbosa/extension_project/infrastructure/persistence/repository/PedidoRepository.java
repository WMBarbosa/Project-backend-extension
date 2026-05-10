package com.barbosa.extension_project.infrastructure.persistence.repository;

import com.barbosa.extension_project.domain.entity.Pedido;
import com.barbosa.extension_project.domain.enums.CanalPedido;
import com.barbosa.extension_project.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pedido> findByUnidadeId(Long unidadeId, Pageable pageable);

    Page<Pedido> findByCanalPedido(CanalPedido canal, Pageable pageable);

    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:canalPedido IS NULL OR p.canalPedido = :canalPedido) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:unidadeId IS NULL OR p.unidade.id = :unidadeId)")
    Page<Pedido> findWithFilters(CanalPedido canalPedido, StatusPedido status,
                                 Long unidadeId, Pageable pageable);
}
