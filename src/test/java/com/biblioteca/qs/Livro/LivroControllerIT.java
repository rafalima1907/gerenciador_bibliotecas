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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class LivroControllerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveCadastrarLivroViaEndpoint() {
        Livro livro = Livro.builder()
                .titulo("Teste de Cobertura")
                .autor("Gemini")
                .isbn("987654321")
                .build();

        ResponseEntity<Livro> response = restTemplate.postForEntity("/livros", livro, Livro.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
    }
}