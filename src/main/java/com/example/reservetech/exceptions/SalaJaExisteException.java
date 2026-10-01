package com.example.reservetech.exceptions;

public class SalaJaExisteException extends RuntimeException {
    public SalaJaExisteException(String mensagem) {
        super(mensagem);
    }
}
