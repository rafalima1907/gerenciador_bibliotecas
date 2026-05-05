package com.biblioteca.qs.Usuario;

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
class UsuarioControllerIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveCadastrarUsuarioViaApi() {
        
        Usuario usuario = new Usuario();
        usuario.setNome("Admin");
        usuario.setEmail("admin@biblioteca.com");
        usuario.setSenha("admin123");

      
        ResponseEntity<Usuario> response =
                restTemplate.postForEntity("/usuarios", usuario, Usuario.class);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void deveRetornarErroParaEmailDuplicado() {
        
        Usuario usuario = new Usuario();
        usuario.setNome("Duplicado");
        usuario.setEmail("duplicado_controller@email.com");
        usuario.setSenha("123");

        
        restTemplate.postForEntity("/usuarios", usuario, Usuario.class);

        
        ResponseEntity<String> response =
                restTemplate.postForEntity("/usuarios", usuario, String.class);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("E-mail já cadastrado");
    }
}
