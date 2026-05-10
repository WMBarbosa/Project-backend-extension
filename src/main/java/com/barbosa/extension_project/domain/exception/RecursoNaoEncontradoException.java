package com.barbosa.extension_project.domain.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    private final String recurso;
    private final Object id;

    public RecursoNaoEncontradoException(String recurso, Object id) {
        super(recurso + " não encontrado(a) com id: " + id);
        this.recurso = recurso;
        this.id = id;
    }

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
        this.recurso = "Recurso";
        this.id = null;
    }

    public String getRecurso() { return recurso; }
    public Object getId() { return id; }
}
