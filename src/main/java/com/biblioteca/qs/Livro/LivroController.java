package com.biblioteca.qs.Livro;

import jakarta.validation.Valid;
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

    @GetMapping("/{id}")
    public Livro buscarPorId(@PathVariable String id) {
        return livroService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livro cadastrar(@Valid @RequestBody Livro livro) {
        return livroService.cadastrar(livro);
    }

    @PutMapping("/{id}")
    public Livro atualizar(@PathVariable String id, @Valid @RequestBody Livro livro) {
        return livroService.atualizar(id, livro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {
        livroService.excluir(id);
    }
}
