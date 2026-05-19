package com.biblioteca.qs.Livro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank(message = "Titulo e obrigatorio")
        @Size(max = 120, message = "Titulo deve ter no maximo 120 caracteres")
        String titulo,

        @NotBlank(message = "Autor e obrigatorio")
        @Size(max = 120, message = "Autor deve ter no maximo 120 caracteres")
        String autor,

        @NotBlank(message = "ISBN e obrigatorio")
        String isbn
) {
    Livro toEntity() {
        return Livro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .build();
    }
}
