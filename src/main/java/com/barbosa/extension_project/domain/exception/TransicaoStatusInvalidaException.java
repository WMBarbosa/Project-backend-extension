package com.barbosa.extension_project.domain.exception;

import com.raizes.domain.enums.StatusPedido;

public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(StatusPedido atual, StatusPedido destino) {
        super("Não é possível alterar o status de " + atual + " para " + destino);
    }
}
