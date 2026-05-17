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
        this.baseUrl = removerBarrasFinais(baseUrl);
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
        String cepNormalizado = manterApenasDigitos(cep);
        if (cepNormalizado.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter 8 digitos validos");
        }
        return cepNormalizado;
    }

    private String removerBarrasFinais(String valor) {
        String normalizado = valor == null ? "" : valor.trim();
        while (normalizado.endsWith("/")) {
            normalizado = normalizado.substring(0, normalizado.length() - 1);
        }
        return normalizado;
    }

    private String manterApenasDigitos(String valor) {
        if (valor == null) {
            return "";
        }

        StringBuilder digitos = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char caractere = valor.charAt(i);
            if (Character.isDigit(caractere)) {
                digitos.append(caractere);
            }
        }
        return digitos.toString();
    }
}
