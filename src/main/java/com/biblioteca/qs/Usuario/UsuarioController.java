package com.biblioteca.qs.Usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/usuarios", "/api/usuarios"})
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final ViaCepClient viaCepClient;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody Usuario usuario) {
        Usuario salvo = service.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(salvo));
    }

    @GetMapping("/cep/{cep}")
    public ViaCepResponse buscarEnderecoPorCep(@PathVariable String cep) {
        return viaCepClient.buscarEnderecoPorCep(cep);
    }
}
