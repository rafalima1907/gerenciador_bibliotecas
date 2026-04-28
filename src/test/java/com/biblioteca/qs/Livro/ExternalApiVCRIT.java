package com.biblioteca.qs.Livro;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@WireMockTest(httpPort = 8080)
class ExternalApiVCRIT {

    @Autowired
    private LivroService livroService;

    @Test
    @DisplayName("RF07 - Deve simular gravação e reprodução VCR via WireMock")
    void deveSimularGravacaoVCR() {
        // Gravando a resposta "na fita"
        stubFor(get(urlEqualTo("/api/books/9788580555332"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"titulo\": \"Engenharia de Software\", \"autor\": \"Pressman\"}")));

        String resultado = livroService.consultarIsbnExterno("9788580555332");

        assertTrue(resultado.contains("Pressman"));
        assertTrue(resultado.contains("Engenharia de Software"));
    }
}