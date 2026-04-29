package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService; 

    @GetMapping
    public List<Livro> listarTodos() {
        return livroService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livro cadastrar(@RequestBody Livro livro) {
        return livroService.cadastrar(livro);
    }
    
    @GetMapping("/isbn/{isbn}")
    public String consultarIsbn(@PathVariable String isbn) {
        return livroService.consultarIsbnExterno(isbn);
    }
}