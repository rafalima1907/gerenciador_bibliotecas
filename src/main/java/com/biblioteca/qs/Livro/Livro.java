package com.biblioteca.qs.Livro;

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
    private String titulo;
    private String autor;
    private String isbn;
}