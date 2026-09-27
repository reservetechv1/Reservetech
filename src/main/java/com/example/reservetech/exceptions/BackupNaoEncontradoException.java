package com.example.reservetech.exceptions;

public class BackupNaoEncontradoException extends RuntimeException {
    public BackupNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
