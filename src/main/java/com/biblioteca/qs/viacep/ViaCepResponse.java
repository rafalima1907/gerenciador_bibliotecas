package com.biblioteca.qs.viacep;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {
    @JsonIgnore
    public boolean encontrado() {
        return !Boolean.TRUE.equals(erro);
    }
}
