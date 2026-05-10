package com.barbosa.extension_project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saldo_fidelidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaldoFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "pontos_acumulados", nullable = false)
    @Builder.Default
    private Integer pontosAcumulados = 0;

    @Column(name = "pontos_resgatados", nullable = false)
    @Builder.Default
    private Integer pontosResgatados = 0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public int getSaldoDisponivel() {
        return pontosAcumulados - pontosResgatados;
    }

    public void acumularPontos(int pontos) {
        this.pontosAcumulados += pontos;
    }

    public void resgatar(int pontos) {
        if (getSaldoDisponivel() < pontos) {
            throw new IllegalStateException(
                "Saldo insuficiente de pontos. Disponível: " + getSaldoDisponivel());
        }
        this.pontosResgatados += pontos;
    }
}
