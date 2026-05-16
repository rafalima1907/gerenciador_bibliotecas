package com.biblioteca.qs;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void deveRetornarServiceUnavailableQuandoBancoEstiverIndisponivel() {
        var response = handler.handleDataAccess();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody())
                .containsEntry("erro", "Banco de dados indisponivel. Verifique se o MongoDB esta em execucao.");
    }

    @Test
    void deveRetornarNotFoundParaMensagemNaoEncontrado() {
        var response = handler.handleBusinessRule(new IllegalArgumentException("Usuario nao encontrado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("erro", "Usuario nao encontrado");
    }

    @Test
    void deveRetornarBadRequestParaRegraDeNegocioGenerica() {
        var response = handler.handleBusinessRule(new IllegalArgumentException("CEP deve conter 8 digitos validos"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("erro", "CEP deve conter 8 digitos validos"));
    }

    @Test
    void deveRetornarBadRequestParaMensagemNula() {
        var response = handler.handleBusinessRule(new IllegalArgumentException((String) null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("erro", "Erro de regra de negocio");
    }
}
