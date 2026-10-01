package com.example.reservetech.repositories;

import com.example.reservetech.model.PerfilUsuario;
import com.example.reservetech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByPerfil(PerfilUsuario perfil);
}