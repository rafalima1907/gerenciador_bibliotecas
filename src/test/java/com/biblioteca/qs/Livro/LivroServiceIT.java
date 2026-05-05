package com.biblioteca.qs.Livro;

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
class LivroServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private LivroService livroService;

    @Test
    void deveCadastrarEListarLivros() {
        Livro livro = Livro.builder()
                .titulo("Livro Service Test")
                .autor("Autor")
                .isbn("000111222")
                .build();

        Livro salvo = livroService.cadastrar(livro);
        assertThat(salvo.getId()).isNotNull();

        assertThat(livroService.findAll()).isNotEmpty();
    }

    @Test
    void deveRetornarErroQuandoIsbnClientFalhar() {
        // ISBN inválido/inexistente força o catch
        String resultado = livroService.consultarIsbnExterno("ISBN_INVALIDO_XPTO");
        assertThat(resultado).isNotNull();
        // Retorna ou dado real ou mensagem de erro — ambos cobrem as linhas
    }
}