package com.biblioteca.qs.Usuario;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "viacep.api.base-url=http://localhost:8089/ws"
)
@Testcontainers
@WireMockTest(httpPort = 8089)
class UsuarioControllerIT {

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
    void deveCadastrarUsuarioViaApi() {
        Usuario usuario = usuario("Admin", "admin@biblioteca.com", "Senha123!");

        ResponseEntity<UsuarioResponse> response =
                restTemplate.postForEntity("/usuarios", usuario, UsuarioResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("admin@biblioteca.com");
    }

    @Test
    void deveRetornarErroParaEmailDuplicado() {
        Usuario usuario = usuario("Duplicado", "duplicado_controller@email.com", "Senha123!");

        restTemplate.postForEntity("/usuarios", usuario, UsuarioResponse.class);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/usuarios", usuario, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("E-mail ja cadastrado");
    }

    @Test
    void deveRetornarBadRequestParaEmailInvalido() {
        Usuario usuario = usuario("Email invalido", "email-invalido", "Senha123!");

        ResponseEntity<String> response =
                restTemplate.postForEntity("/usuarios", usuario, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("E-mail deve ter formato valido");
    }

    @Test
    void deveConsultarEnderecoPorCepViaApi() {
        stubFor(get(urlEqualTo("/ws/01001000/json/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "cep": "01001-000",
                                  "logradouro": "Praca da Se",
                                  "bairro": "Se",
                                  "localidade": "Sao Paulo",
                                  "uf": "SP"
                                }
                                """)));

        ResponseEntity<ViaCepResponse> response =
                restTemplate.getForEntity("/api/usuarios/cep/01001000", ViaCepResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().logradouro()).isEqualTo("Praca da Se");
        assertThat(response.getBody().bairro()).isEqualTo("Se");
        assertThat(response.getBody().localidade()).isEqualTo("Sao Paulo");
        assertThat(response.getBody().uf()).isEqualTo("SP");
    }

    private Usuario usuario(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        return usuario;
    }
}
