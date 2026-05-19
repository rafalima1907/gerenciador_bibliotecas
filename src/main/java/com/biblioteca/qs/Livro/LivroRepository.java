package com.biblioteca.qs.Livro;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface LivroRepository extends MongoRepository<Livro, String> {
    List<Livro> findByUsuarioId(String usuarioId);
    
    Optional<Livro> findByIdAndUsuarioId(String id, String usuarioId);
    
    Optional<Livro> findByIsbnAndUsuarioId(String isbn, String usuarioId);
}