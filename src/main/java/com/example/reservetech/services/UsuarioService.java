package com.example.reservetech.services;

import com.example.reservetech.DTO.RedefinirSenhaDTO;
import com.example.reservetech.DTO.UsuarioResponseDTO;
import com.example.reservetech.DTO.UsuarioUpdateDTO;
import com.example.reservetech.exceptions.UsuarioNaoEncontradoException;
import com.example.reservetech.model.PerfilUsuario;
import com.example.reservetech.model.Usuario;
import com.example.reservetech.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioResponseDTO::new);
    }

    public List<UsuarioResponseDTO> listarProfessores() {
        return usuarioRepository.findByPerfil(PerfilUsuario.PROFESSOR)
                .stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return new UsuarioResponseDTO(buscarEntidadePorId(id));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = buscarEntidadePorId(id);
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setPerfil(dto.perfil());

        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.delete(buscarEntidadePorId(id));
    }

    public void redefinirSenha(Long id, RedefinirSenhaDTO dto) {
        Usuario usuario = buscarEntidadePorId(id);
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }
}