package com.biblioteca.qs.Usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class UsuarioServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("teste_unico_service@email.com");
        usuario.setSenha("123");

        // Act
        Usuario salvo = usuarioService.cadastrar(usuario);

        // Assert
        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Teste");
        assertThat(salvo.getEmail()).isEqualTo("teste_unico_service@email.com");
    }

    @Test
    void deveLancarExcecaoParaEmailDuplicado() {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setNome("User1");
        u1.setEmail("duplicado_service@email.com");
        u1.setSenha("123");

        usuarioService.cadastrar(u1);

        Usuario u2 = new Usuario();
        u2.setNome("User2");
        u2.setEmail("duplicado_service@email.com");
        u2.setSenha("456");

        // Act + Assert
        assertThatThrownBy(() -> usuarioService.cadastrar(u2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("E-mail já cadastrado");
    }
}