package com.example.reservetech.services;

import com.example.reservetech.DTO.UsuarioRequestDTO;
import com.example.reservetech.DTO.UsuarioResponseDTO;
import com.example.reservetech.model.Usuario;
import com.example.reservetech.repositories.UsuarioRepository;
import com.example.reservetech.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthService(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                passwordEncoder.encode(dto.senha()),
                dto.perfil()
        );
        usuario.setPrecisaTrocarSenha(true);
        usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario);
    }

    public String login(String email, String senha) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, senha)
        );
        return jwtService.gerarToken(email);
    }
}
