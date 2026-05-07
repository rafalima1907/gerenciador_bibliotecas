package com.biblioteca.qs.Usuario;

import org.junit.jupiter.api.BeforeEach;
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
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveCadastrarUsuarioComSucessoECriptografarSenha() {
        Usuario usuario = usuario("Teste", "TESTE_UNICO_SERVICE@EMAIL.COM", "Senha123!");

        Usuario salvo = usuarioService.cadastrar(usuario);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Teste");
        assertThat(salvo.getEmail()).isEqualTo("teste_unico_service@email.com");
        assertThat(salvo.getSenha()).isNotEqualTo("Senha123!");
        assertThat(salvo.getSenha()).startsWith("$2");
    }

    @Test
    void deveAutenticarUsuarioComSenhaCorreta() {
        usuarioService.cadastrar(usuario("Teste", "login@email.com", "Senha123!"));

        Usuario autenticado = usuarioService.autenticar("login@email.com", "Senha123!");

        assertThat(autenticado.getEmail()).isEqualTo("login@email.com");
    }

    @Test
    void deveLancarExcecaoParaSenhaIncorreta() {
        usuarioService.cadastrar(usuario("Teste", "login@email.com", "Senha123!"));

        assertThatThrownBy(() -> usuarioService.autenticar("login@email.com", "senha-errada"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciais invalidas");
    }

    @Test
    void deveLancarExcecaoParaEmailDuplicado() {
        usuarioService.cadastrar(usuario("User1", "duplicado_service@email.com", "Senha123!"));

        assertThatThrownBy(() -> usuarioService.cadastrar(usuario("User2", "duplicado_service@email.com", "Outra123!")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail ja cadastrado");
    }

    private Usuario usuario(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        return usuario;
    }
}
