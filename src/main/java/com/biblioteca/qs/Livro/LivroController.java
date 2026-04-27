package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {
    private LivroRepository livroRepository;
    LivroService livroService;
    @GetMapping
    public ResponseEntity<List<Livro>> getAllLivros(){
        return ResponseEntity.ok(this.livroService.findAll());
    }
}
