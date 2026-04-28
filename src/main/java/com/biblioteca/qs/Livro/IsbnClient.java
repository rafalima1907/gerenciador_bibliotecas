package com.biblioteca.qs.Livro;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IsbnClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public String buscarDadosPorIsbn(String isbn) {
        // O localhost permite que o WireMock intercepte a chamada no teste (VCR)
        String url = "http://localhost:8080/api/books/" + isbn;
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "Erro ao consultar API externa";
        }
    }
}