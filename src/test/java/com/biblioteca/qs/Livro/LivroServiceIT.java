package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class LivroServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private LivroService livroService;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveCadastrarEListarLivros() {
        Livro livro = Livro.builder()
                .titulo("Livro Service Test")
                .autor("Autor")
                .isbn("9788580555332")
                .build();

        Livro salvo = livroService.cadastrar(livro);
        assertThat(salvo.getId()).isNotNull();
        assertThat(livroService.findAll()).isNotEmpty();
    }

    @Test
    void deveNormalizarIsbnAntesDePersistir() {
        Livro livro = Livro.builder()
                .titulo("Livro com hifen")
                .autor("Autor")
                .isbn("978-85-8055-533-2")
                .build();

        Livro salvo = livroService.cadastrar(livro);

        assertThat(salvo.getIsbn()).isEqualTo("9788580555332");
    }

    @ParameterizedTest
    @CsvSource({
            "123",
            "ABC",
            "97885805553"
    })
    void deveRejeitarIsbnInvalido(String isbn) {
        Livro livro = Livro.builder()
                .titulo("Livro invalido")
                .autor("Autor")
                .isbn(isbn)
                .build();

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN deve conter 10 ou 13 digitos validos");
    }

    @Test
    void deveRejeitarIsbnDuplicado() {
        livroService.cadastrar(livro("Livro 1", "Autor", "9788580555332"));

        assertThatThrownBy(() -> livroService.cadastrar(livro("Livro 2", "Autor", "9788580555332")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN ja cadastrado");
    }

    @Test
    void deveRetornarErroQuandoIsbnClientFalhar() {
        String resultado = livroService.consultarIsbnExterno("9788580555332");

        assertThat(resultado).contains("Erro ao consultar ISBN");
    }

    private Livro livro(String titulo, String autor, String isbn) {
        return Livro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .build();
    }
}
