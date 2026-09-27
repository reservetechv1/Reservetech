package com.example.reservetech.DTO;

import com.example.reservetech.model.PerfilUsuario;
import com.example.reservetech.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        boolean ativo,
        boolean precisaTrocarSenha
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil(), usuario.isAtivo(), usuario.isPrecisaTrocarSenha());
    }
}
