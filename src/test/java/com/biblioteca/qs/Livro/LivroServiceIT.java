package com.biblioteca.qs.Livro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

    private static final String USER_ID = "user123";

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    void deveCadastrarEListarLivrosDoUsuario() {
        Livro livro = livro("Livro Service Test", "Autor", "9788580555332");

        Livro salvo = livroService.cadastrar(livro, USER_ID);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getUsuarioId()).isEqualTo(USER_ID);
        assertThat(livroService.findAll(USER_ID)).isNotEmpty();

        assertThat(livroService.findAll("outro_usuario")).isEmpty();
    }

    @Test
    void deveNormalizarIsbnAntesDePersistir() {
        Livro livro = livro("Livro com hifen", "Autor", "978-85-8055-533-2");
        Livro salvo = livroService.cadastrar(livro, USER_ID);
        assertThat(salvo.getIsbn()).isEqualTo("9788580555332");
    }

    @Test
    void deveAceitarIsbn10ComXFinal() {
        Livro salvo = livroService.cadastrar(livro("Livro ISBN 10", "Autor", "0-306-40615-x"), USER_ID);
        assertThat(salvo.getIsbn()).isEqualTo("030640615X");
    }

    @Test
    void deveAceitarIsbn10ApenasNumerico() {
        Livro salvo = livroService.cadastrar(livro("Livro ISBN 10 numerico", "Autor", "0306406152"), USER_ID);
        assertThat(salvo.getIsbn()).isEqualTo("0306406152");
    }

    @ParameterizedTest
    @CsvSource({
            "123",
            "ABC",
            "97885805553",
            "12345678X0"
    })
    void deveRejeitarIsbnInvalido(String isbn) {
        Livro livro = livro("Livro invalido", "Autor", isbn);

        assertThatThrownBy(() -> livroService.cadastrar(livro, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN deve conter 10 ou 13 digitos validos");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deveRejeitarIsbnNuloOuVazio(String isbn) {
        Livro livro = livro("Livro invalido", "Autor", isbn);

        assertThatThrownBy(() -> livroService.cadastrar(livro, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN deve conter 10 ou 13 digitos validos");
    }

    @Test
    void deveRejeitarIsbnDuplicadoParaOMesmoUsuario() {
        livroService.cadastrar(livro("Livro 1", "Autor", "9788580555332"), USER_ID);
        Livro livroDuplicado = livro("Livro 2", "Autor", "9788580555332");

        assertThatThrownBy(() -> livroService.cadastrar(livroDuplicado, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN ja cadastrado");
    }

    @Test
    void devePermitirMesmoIsbnParaUsuariosDiferentes() {
        livroService.cadastrar(livro("Livro 1", "Autor", "9788580555332"), USER_ID);
        Livro livroOutroUsuario = livroService.cadastrar(livro("Livro 2", "Autor", "9788580555332"), "outroUser");

        assertThat(livroOutroUsuario.getId()).isNotNull();
    }

    @Test
    void deveAtualizarLivroMantendoMesmoIsbn() {
        Livro salvo = livroService.cadastrar(livro("Titulo antigo", "Autor", "9788580555332"), USER_ID);

        Livro atualizado = livroService.atualizar(salvo.getId(), livro("Titulo novo", "Autor novo", "9788580555332"),
                USER_ID);

        assertThat(atualizado.getId()).isEqualTo(salvo.getId());
        assertThat(atualizado.getTitulo()).isEqualTo("Titulo novo");
        assertThat(atualizado.getIsbn()).isEqualTo("9788580555332");
    }

    @Test
    void deveRejeitarAtualizacaoComIsbnDeOutroLivroDoMesmoUsuario() {
        Livro primeiro = livroService.cadastrar(livro("Livro 1", "Autor", "9788580555332"), USER_ID);
        livroService.cadastrar(livro("Livro 2", "Autor", "9780134685991"), USER_ID);
        Livro livroAtualizado = livro("Livro 1", "Autor", "9780134685991");

        assertThatThrownBy(() -> livroService.atualizar(primeiro.getId(), livroAtualizado, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN ja cadastrado");
    }

    private Livro livro(String titulo, String autor, String isbn) {
        return Livro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .build();
    }
}
