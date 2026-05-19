package com.biblioteca.qs.Livro;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;
    private static final String USUARIO_ID = "USUARIO_ID";

    @GetMapping
    public ResponseEntity<List<Livro>> listarTodos(HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        return ResponseEntity.ok(livroService.findAll(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable String id, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        return ResponseEntity.ok(livroService.findById(id, usuarioId));
    }

    @PostMapping
    public ResponseEntity<Livro> cadastrar(@Valid @RequestBody Livro livro, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.cadastrar(livro, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable String id, @Valid @RequestBody Livro livro, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        return ResponseEntity.ok(livroService.atualizar(id, livro, usuarioId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        livroService.excluir(id, usuarioId);
    }
    
    private String obterUsuarioId(HttpSession session) {
        String usuarioId = (String) session.getAttribute(USUARIO_ID);
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario nao autenticado");
        }
        return usuarioId;
    }
}