package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class LivroRepositoryIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private LivroRepository repository;

    @Test
    void deveSalvarLivroNoMongoDBRealSemMocks() {
        // Usando o Builder para evitar erros de construtor no VS Code
        Livro livro = Livro.builder()
                .titulo("Dom Casmurro")
                .autor("Machado de Assis")
                .isbn("123456789")
                .build();

        // Persistência real
        Livro salvo = repository.save(livro);

        // Validação usando os métodos do Lombok
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getTitulo()).isEqualTo("Dom Casmurro");
    }
}