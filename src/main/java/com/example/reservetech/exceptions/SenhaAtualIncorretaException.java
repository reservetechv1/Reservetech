package com.example.reservetech.exceptions;

public class SenhaAtualIncorretaException extends RuntimeException {
    public SenhaAtualIncorretaException(String mensagem) {
        super(mensagem);
    }
}
