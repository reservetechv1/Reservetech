package com.example.reservetech.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaDTO(
        @NotBlank @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres") String novaSenha
) {
}
