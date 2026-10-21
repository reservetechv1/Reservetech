package com.example.reservetech.exceptions;

public class ReservaForaDoPrazoException extends RuntimeException {
    public ReservaForaDoPrazoException(String mensagem) {
        super(mensagem);
    }
}