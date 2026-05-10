package com.barbosa.extension_project.domain.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    private final Long produtoId;
    private final int disponivel;
    private final int solicitado;

    public EstoqueInsuficienteException(Long produtoId, int disponivel, int solicitado) {
        super("Estoque insuficiente para produto id=" + produtoId +
              ". Disponível: " + disponivel + ", Solicitado: " + solicitado);
        this.produtoId = produtoId;
        this.disponivel = disponivel;
        this.solicitado = solicitado;
    }

    public Long getProdutoId() { return produtoId; }
    public int getDisponivel() { return disponivel; }
    public int getSolicitado() { return solicitado; }
}
