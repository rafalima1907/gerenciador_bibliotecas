package com.biblioteca.qs.Usuario;

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
class AuthControllerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveRegistrarUsuarioEConsultarSessaoAtual() {
        Usuario usuario = usuario("Sessao", "sessao@email.com", "Senha123!");

        ResponseEntity<UsuarioResponse> registro =
                restTemplate.postForEntity("/api/auth/register", usuario, UsuarioResponse.class);

        assertThat(registro.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        List<String> cookies = registro.getHeaders().get(HttpHeaders.SET_COOKIE);

        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);

        ResponseEntity<UsuarioResponse> me = restTemplate.exchange(
                "/api/auth/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UsuarioResponse.class
        );

        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getBody()).isNotNull();
        assertThat(me.getBody().email()).isEqualTo("sessao@email.com");
    }

    @Test
    void deveRealizarLoginComCredenciaisValidas() {
        restTemplate.postForEntity("/api/auth/register", usuario("Login", "login@email.com", "Senha123!"), UsuarioResponse.class);

        ResponseEntity<UsuarioResponse> response =
                restTemplate.postForEntity("/api/auth/login", new LoginRequest("login@email.com", "Senha123!"), UsuarioResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("login@email.com");
    }

    @Test
    void deveRecusarLoginComCredenciaisInvalidas() {
        restTemplate.postForEntity("/api/auth/register", usuario("Login", "login@email.com", "Senha123!"), UsuarioResponse.class);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/auth/login", new LoginRequest("login@email.com", "errada"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Credenciais invalidas");
    }

    private Usuario usuario(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        return usuario;
    }
}
