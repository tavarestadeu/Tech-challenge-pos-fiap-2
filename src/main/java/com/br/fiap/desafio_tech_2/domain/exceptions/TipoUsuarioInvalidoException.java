package com.br.fiap.desafio_tech_2.domain.exceptions;

public class TipoUsuarioInvalidoException extends RuntimeException {
    public TipoUsuarioInvalidoException(String message) {
        super(message);
    }
}