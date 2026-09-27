package com.example.reservetech.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenericExcepion(Exception e) {
        logger.error("Erro interno não tratado:", e);
        ErroResponse erro = new ErroResponse(500, "Erro interno no servidor.", LocalDateTime.now());
        return ResponseEntity.status(500).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> fieldNotValid(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "Campo inválido";

        ErroResponse erro = new ErroResponse(400, mensagem, LocalDateTime.now());
        return ResponseEntity.status(400).body(erro);
    }

    @ExceptionHandler(DispositivoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> dispositivoNaoEncontrado(DispositivoNaoEncontradoException e) {
        ErroResponse erro = new ErroResponse(404, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(404).body(erro);
    }

    @ExceptionHandler(SalaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> salaNaoEncontrada(SalaNaoEncontradaException e) {
        ErroResponse erro = new ErroResponse(404, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(404).body(erro);
    }

    @ExceptionHandler(ReservaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> reservaNaoEncontrada(ReservaNaoEncontradaException e) {
        ErroResponse erro = new ErroResponse(404, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(404).body(erro);
    }

    @ExceptionHandler(ConflitoDeHorarioException.class)
    public ResponseEntity<ErroResponse> conflitoDeHorario(ConflitoDeHorarioException e) {
        ErroResponse erro = new ErroResponse(409, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(409).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(BadCredentialsException e) {
        ErroResponse erro = new ErroResponse(401, "E-mail ou senha inválidos", LocalDateTime.now());
        return ResponseEntity.status(401).body(erro);
    }

    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public ResponseEntity<ErroResponse> handleContaDesativada(org.springframework.security.authentication.DisabledException e) {
        ErroResponse erro = new ErroResponse(403, "Sua conta está desativada. Entre em contato com o TI da faculdade.", LocalDateTime.now());
        return ResponseEntity.status(403).body(erro);
    }

    @ExceptionHandler(SenhaAtualIncorretaException.class)
    public ResponseEntity<ErroResponse> handleSenhaAtualIncorreta(SenhaAtualIncorretaException e) {
        ErroResponse erro = new ErroResponse(400, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(400).body(erro);
    }

    @ExceptionHandler(BackupNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> backupNaoEncontrado(BackupNaoEncontradoException e) {
        ErroResponse erro = new ErroResponse(404, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(404).body(erro);
    }
}
