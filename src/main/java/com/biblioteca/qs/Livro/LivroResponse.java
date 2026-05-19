package com.biblioteca.qs.Livro;

public record LivroResponse(
        String id,
        String titulo,
        String autor,
        String isbn
) {
    static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn()
        );
    }
}
