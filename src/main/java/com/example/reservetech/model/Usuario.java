package com.example.reservetech.model;

import com.example.reservetech.model.PerfilUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    // Usuário desativado não consegue mais logar, mas continua no banco
    // (mantém o histórico de reservas ligado a ele). Usado no lugar de excluir.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean ativo = true;

    // Marca que o usuário precisa trocar a senha no próximo login
    // (senha padrão de cadastro, ou redefinida pelo TI). Default false
    // pro banco não forçar troca em quem já está usando o sistema.
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean precisaTrocarSenha = false;

    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
