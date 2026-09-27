package com.example.reservetech.controller;

import com.example.reservetech.DTO.AlterarSenhaDTO;
import com.example.reservetech.DTO.RedefinirSenhaDTO;
import com.example.reservetech.DTO.UsuarioResponseDTO;
import com.example.reservetech.DTO.UsuarioUpdateDTO;
import com.example.reservetech.model.Usuario;
import com.example.reservetech.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> meusDados(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioLogado));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @GetMapping("/professores")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarProfessores() {
        return ResponseEntity.ok(usuarioService.listarProfessores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        usuarioService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/desativar-varios")
    public ResponseEntity<Void> desativarVarios(@RequestBody List<Long> ids) {
        usuarioService.desativarVarios(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/ativar-varios")
    public ResponseEntity<Void> ativarVarios(@RequestBody List<Long> ids) {
        usuarioService.ativarVarios(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarMinhaSenha(@AuthenticationPrincipal Usuario usuarioLogado, @RequestBody @Valid AlterarSenhaDTO dto) {
        usuarioService.alterarMinhaSenha(usuarioLogado, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@PathVariable Long id, @RequestBody @Valid RedefinirSenhaDTO dto) {
        usuarioService.redefinirSenha(id, dto);
        return ResponseEntity.noContent().build();
    }
}
