package com.barbosa.extension_project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_unidades",
       uniqueConstraints = @UniqueConstraint(columnNames = {"unidade_id", "produto_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstoqueUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 0;

    @Column(name = "quantidade_minima")
    @Builder.Default
    private Integer quantidadeMinima = 0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public boolean temSaldo(int qtdSolicitada) {
        return quantidade >= qtdSolicitada;
    }

    public void reduzir(int qtd) {
        if (!temSaldo(qtd)) {
            throw new IllegalStateException(
                "Estoque insuficiente para produto id=" + produto.getId() +
                ". Disponível: " + quantidade + ", Solicitado: " + qtd);
        }
        this.quantidade -= qtd;
    }

    public void incrementar(int qtd) {
        this.quantidade += qtd;
    }
}
