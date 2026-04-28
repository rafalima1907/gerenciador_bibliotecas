package com.biblioteca.qs.Usuario;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    // Essencial para login e validação de e-mail único
    Optional<Usuario> findByEmail(String email);
}