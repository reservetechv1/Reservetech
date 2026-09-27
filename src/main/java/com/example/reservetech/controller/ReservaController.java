package com.example.reservetech.controller;


import com.example.reservetech.DTO.AtualizarStatusReservaDTO;
import com.example.reservetech.DTO.ReservaRequestDTO;
import com.example.reservetech.DTO.ReservaResponseDTO;
import com.example.reservetech.DTO.ReservaUpdateDTO;
import com.example.reservetech.model.StatusReserva;
import com.example.reservetech.model.Usuario;
import com.example.reservetech.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(
            @RequestBody @Valid ReservaRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(201).body(reservaService.criar(dto, usuarioLogado));
    }

    @GetMapping("/minhas")
    public ResponseEntity<Page<ReservaResponseDTO>> listarMinhasReservas(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(required = false) String periodo,
            Pageable pageable
    ) {
        if (periodo != null) {
            return ResponseEntity.ok(reservaService.listarMinhasReservasPorPeriodo(usuarioLogado.getId(), periodo, pageable));
        }
        return ResponseEntity.ok(reservaService.listarMinhasReservas(usuarioLogado.getId(), pageable));
    }

    @GetMapping
    public ResponseEntity<Page<ReservaResponseDTO>> listarTodas(
            @RequestParam(required = false) StatusReserva status,
            @RequestParam(required = false) LocalDate data,
            Pageable pageable
    ) {
        if (data != null) {
            return ResponseEntity.ok(reservaService.listarPorData(data, pageable));
        }
        if (status != null) {
            return ResponseEntity.ok(reservaService.listarPorStatus(status, pageable));
        }
        return ResponseEntity.ok(reservaService.listarTodas(pageable));
    }

    @GetMapping("/contagem-pendentes")
    public ResponseEntity<Long> contarPendentes() {
        return ResponseEntity.ok(reservaService.contarPendentes());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Page<ReservaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusReservaDTO dto
    ) {
        return ResponseEntity.ok(reservaService.atualizarStatus(id, dto.status()));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelarPropriaReserva(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(reservaService.cancelarPropriaReserva(id, usuarioLogado));
    }



    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaUpdateDTO dto
    ) {
        return ResponseEntity.ok(reservaService.atualizar(id, dto));
    }
}