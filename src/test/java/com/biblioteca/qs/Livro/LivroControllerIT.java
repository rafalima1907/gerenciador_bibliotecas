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
import java.util.Map;

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

    private HttpHeaders headersComSessao;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        headersComSessao = obterHeadersComSessaoValida();
    }

    private HttpHeaders obterHeadersComSessaoValida() {
        Map<String, String> novoUsuario = Map.of(
                "nome", "Usuario Teste",
                "email", "teste@teste.com",
                "senha", "senha1234"
        );
        restTemplate.postForEntity("/api/auth/register", novoUsuario, Void.class);

        Map<String, String> credenciais = Map.of(
                "email", "teste@teste.com",
                "senha", "senha1234"
        );
        ResponseEntity<Void> loginResponse = restTemplate.postForEntity("/api/auth/login", credenciais, Void.class);
        
        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        HttpHeaders headers = new HttpHeaders();
        if (cookie != null) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void deveCadastrarLivroViaEndpoint() {
        Livro livro = livro("Teste de Cobertura Final", "Jose Carlos", "9788580555332");
        HttpEntity<Livro> request = new HttpEntity<>(livro, headersComSessao);

        ResponseEntity<Livro> response = restTemplate.postForEntity("/api/livros", request, Livro.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getIsbn()).isEqualTo("9788580555332");
        assertThat(response.getBody().getUsuarioId()).isNotNull();
    }

    @Test
    void deveListarLivrosViaEndpoint() {
        HttpEntity<Livro> requestCadastro = new HttpEntity<>(livro("Livro para Listagem", "Autor Teste", "9780134685991"), headersComSessao);
        restTemplate.postForEntity("/api/livros", requestCadastro, Livro.class);

        HttpEntity<Void> requestBusca = new HttpEntity<>(headersComSessao);
        ResponseEntity<List> response = restTemplate.exchange("/api/livros", HttpMethod.GET, requestBusca, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void deveAtualizarLivroViaEndpoint() {
        HttpEntity<Livro> requestCadastro = new HttpEntity<>(livro("Titulo antigo", "Autor antigo", "9780132350884"), headersComSessao);
        Livro salvo = restTemplate.postForEntity("/api/livros", requestCadastro, Livro.class).getBody();

        Livro atualizado = livro("Clean Code", "Robert C. Martin", "9780132350884");
        HttpEntity<Livro> requestAtualizacao = new HttpEntity<>(atualizado, headersComSessao);
        
        ResponseEntity<Livro> response = restTemplate.exchange(
                "/api/livros/" + salvo.getId(),
                HttpMethod.PUT,
                requestAtualizacao,
                Livro.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitulo()).isEqualTo("Clean Code");
    }

    @Test
    void deveExcluirLivroViaEndpoint() {
        HttpEntity<Livro> requestCadastro = new HttpEntity<>(livro("Livro para excluir", "Autor", "9780321125217"), headersComSessao);
        Livro salvo = restTemplate.postForEntity("/api/livros", requestCadastro, Livro.class).getBody();

        HttpEntity<Void> requestExclusao = new HttpEntity<>(headersComSessao);
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/livros/" + salvo.getId(),
                HttpMethod.DELETE,
                requestExclusao,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void deveRetornarConflitoParaIsbnDuplicado() {
        HttpEntity<Livro> request1 = new HttpEntity<>(livro("Livro 1", "Autor", "9788580555332"), headersComSessao);
        restTemplate.postForEntity("/api/livros", request1, Livro.class);

        HttpEntity<Livro> request2 = new HttpEntity<>(livro("Livro 2", "Outro autor", "9788580555332"), headersComSessao);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/livros", request2, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue(); 
        assertThat(response.getBody()).contains("ISBN ja cadastrado");
    }

    @Test
    void deveRecusarRequisicaoSemSessao() {
        Livro livro = livro("Teste Sem Sessao", "Autor", "9788580555332");
        HttpEntity<Livro> requestSemSessao = new HttpEntity<>(livro); 

        ResponseEntity<String> response = restTemplate.postForEntity("/api/livros", requestSemSessao, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    private Livro livro(String titulo, String autor, String isbn) {
        return Livro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .build();
    }
}