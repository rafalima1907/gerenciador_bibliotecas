package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class LivroControllerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveCadastrarLivroViaEndpoint() {
        Livro livro = Livro.builder()
                .titulo("Teste de Cobertura Final")
                .autor("José Carlos")
                .isbn("987654321")
                .build();

        ResponseEntity<Livro> response = restTemplate.postForEntity("/api/livros", livro, Livro.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void deveListarLivrosViaEndpoint() {
        Livro livro = Livro.builder()
                .titulo("Livro para Listagem")
                .autor("Autor Teste")
                .isbn("111222333")
                .build();
        restTemplate.postForEntity("/api/livros", livro, Livro.class);

        ResponseEntity<List> response = restTemplate.getForEntity("/api/livros", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}