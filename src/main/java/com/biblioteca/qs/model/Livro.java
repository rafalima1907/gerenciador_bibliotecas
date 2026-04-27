package com.biblioteca.qs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder // Adicionamos o Builder para facilitar a criação no teste
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "livros")
public class Livro {
    @Id
    private String id;
    private String titulo;
    private String autor;
    private String isbn;
}