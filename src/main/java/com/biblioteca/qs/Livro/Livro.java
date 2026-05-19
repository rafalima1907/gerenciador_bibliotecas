package com.biblioteca.qs.Livro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "livros")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Livro {
    @Id
    private String id;

    @NotBlank(message = "Titulo e obrigatorio")
    @Size(max = 120, message = "Titulo deve ter no maximo 120 caracteres")
    private String titulo;

    @NotBlank(message = "Autor e obrigatorio")
    @Size(max = 120, message = "Autor deve ter no maximo 120 caracteres")
    private String autor;

    @NotBlank(message = "ISBN e obrigatorio")
    private String isbn;
    
    private String usuarioId;
}