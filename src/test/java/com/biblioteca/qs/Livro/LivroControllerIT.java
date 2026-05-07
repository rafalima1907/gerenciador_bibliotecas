package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
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

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveCadastrarLivroViaEndpoint() {
        Livro livro = Livro.builder()
                .titulo("Teste de Cobertura Final")
                .autor("Jose Carlos")
                .isbn("9788580555332")
                .build();

        ResponseEntity<Livro> response = restTemplate.postForEntity("/api/livros", livro, Livro.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getIsbn()).isEqualTo("9788580555332");
    }

    @Test
    void deveListarLivrosViaEndpoint() {
        restTemplate.postForEntity("/api/livros", livro("Livro para Listagem", "Autor Teste", "9780134685991"), Livro.class);

        ResponseEntity<List> response = restTemplate.getForEntity("/api/livros", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void deveAtualizarLivroViaEndpoint() {
        Livro salvo = restTemplate.postForEntity(
                "/api/livros",
                livro("Titulo antigo", "Autor antigo", "9780132350884"),
                Livro.class
        ).getBody();

        Livro atualizado = livro("Clean Code", "Robert C. Martin", "9780132350884");
        ResponseEntity<Livro> response = restTemplate.exchange(
                "/api/livros/" + salvo.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(atualizado),
                Livro.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitulo()).isEqualTo("Clean Code");
    }

    @Test
    void deveExcluirLivroViaEndpoint() {
        Livro salvo = restTemplate.postForEntity(
                "/api/livros",
                livro("Livro para excluir", "Autor", "9780321125217"),
                Livro.class
        ).getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/livros/" + salvo.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void deveRetornarConflitoParaIsbnDuplicado() {
        Livro livro = livro("Livro 1", "Autor", "9788580555332");
        restTemplate.postForEntity("/api/livros", livro, Livro.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/livros",
                livro("Livro 2", "Outro autor", "9788580555332"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("ISBN ja cadastrado");
    }

    @Test
    void deveRetornarBadRequestParaLivroInvalido() {
        Livro livro = livro("", "Autor", "123");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/livros", livro, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Titulo e obrigatorio");
    }

    private Livro livro(String titulo, String autor, String isbn) {
        return Livro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .build();
    }
}
