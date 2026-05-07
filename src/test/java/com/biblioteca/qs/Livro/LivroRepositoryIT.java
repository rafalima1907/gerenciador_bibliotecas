package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.BeforeEach;
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
class LivroRepositoryIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveSalvarLivroNoMongoDBRealSemMocks() {
        Livro livro = Livro.builder()
                .titulo("Dom Casmurro")
                .autor("Machado de Assis")
                .isbn("9788535910663")
                .build();

        Livro salvo = repository.save(livro);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getTitulo()).isEqualTo("Dom Casmurro");
    }

    @Test
    void deveBuscarLivroPorIsbn() {
        repository.save(Livro.builder()
                .titulo("Engenharia de Software")
                .autor("Pressman")
                .isbn("9788580555332")
                .build());

        assertThat(repository.findByIsbn("9788580555332")).isPresent();
    }
}
