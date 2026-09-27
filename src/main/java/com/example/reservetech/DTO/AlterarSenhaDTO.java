package com.example.reservetech.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
        @NotBlank(message = "Informe a senha atual") String senhaAtual,
        @NotBlank @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres") String novaSenha
) {
}
