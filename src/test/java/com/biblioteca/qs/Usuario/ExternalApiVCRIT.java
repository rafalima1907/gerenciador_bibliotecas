package com.biblioteca.qs.Usuario;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "viacep.api.base-url=http://localhost:8089/ws")
@Testcontainers
@WireMockTest(httpPort = 8089)
class ExternalApiVCRIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("RF11 - Deve reproduzir dados externos do ViaCEP com WireMock/VCR")
    void deveSimularConsultaViaCepNoCadastroUsuario() {
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

        Usuario salvo = usuarioService.cadastrar(usuarioComCep("ViaCEP", "viacep@email.com", "Senha123!", "01001-000"));

        assertThat(salvo.getCep()).isEqualTo("01001-000");
        assertThat(salvo.getLogradouro()).isEqualTo("Praca da Se");
        assertThat(salvo.getBairro()).isEqualTo("Se");
        assertThat(salvo.getLocalidade()).isEqualTo("Sao Paulo");
        assertThat(salvo.getUf()).isEqualTo("SP");
        verify(getRequestedFor(urlEqualTo("/ws/01001000/json/")));
    }

    @Test
    void deveRejeitarCepComFormatoInvalidoAntesDaConsultaExterna() {
        assertThatThrownBy(() -> viaCepClient.buscarEnderecoPorCep("123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CEP deve conter 8 digitos validos");
    }

    @Test
    void deveRetornarErroQuandoViaCepInformarCepNaoEncontrado() {
        stubFor(get(urlEqualTo("/ws/99999999/json/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"erro\": true}")));

        assertThatThrownBy(() -> viaCepClient.buscarEnderecoPorCep("99999-999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CEP nao encontrado");
    }

    @Test
    void deveRetornarErroQuandoViaCepEstiverIndisponivel() {
        stubFor(get(urlEqualTo("/ws/01001000/json/"))
                .willReturn(aResponse().withStatus(503)));

        assertThatThrownBy(() -> viaCepClient.buscarEnderecoPorCep("01001-000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nao foi possivel consultar o CEP");
    }

    private Usuario usuarioComCep(String nome, String email, String senha, String cep) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setCep(cep);
        return usuario;
    }
}
