package com.biblioteca.qs.viacep;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ViaCepClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ViaCepClient(@Value("${viacep.api.base-url:https://viacep.com.br/ws}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        String cepNormalizado = normalizarCep(cep);
        String url = baseUrl + "/" + cepNormalizado + "/json/";

        try {
            ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);
            if (response == null || !response.encontrado()) {
                throw new IllegalArgumentException("CEP nao encontrado");
            }
            return response;
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Nao foi possivel consultar o CEP");
        }
    }

    private String normalizarCep(String cep) {
        String cepNormalizado = cep == null ? "" : cep.replaceAll("\\D", "");
        if (!cepNormalizado.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve conter 8 digitos validos");
        }
        return cepNormalizado;
    }
}
