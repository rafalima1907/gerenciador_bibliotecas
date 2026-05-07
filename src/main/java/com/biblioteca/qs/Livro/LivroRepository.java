package com.biblioteca.qs.Livro;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LivroRepository extends MongoRepository<Livro, String> {
    Optional<Livro> findByIsbn(String isbn);
}
