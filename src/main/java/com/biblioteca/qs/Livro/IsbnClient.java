package com.biblioteca.qs.Livro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IsbnClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public IsbnClient(@Value("${isbn.api.base-url:http://localhost:8080/api/books}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public String buscarDadosPorIsbn(String isbn) {
        String url = baseUrl + "/" + isbn;
        return restTemplate.getForObject(url, String.class);
    }
}
