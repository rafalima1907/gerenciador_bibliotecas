package com.biblioteca.qs.Usuario; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        // Chama a lógica de "Caixa Branca" (E-mail único) antes de salvar
        return ResponseEntity.ok(service.cadastrar(usuario));
    }
}