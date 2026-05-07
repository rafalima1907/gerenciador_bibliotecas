package com.biblioteca.qs.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ter formato valido")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        String senha
) {
}
