package com.biblioteca.qs.repository;

import com.biblioteca.qs.model.Livro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LivroRepository extends MongoRepository<Livro, String> {
}
