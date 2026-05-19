package com.biblioteca.qs.Livro;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;
    private static final String USUARIO_ID = "USUARIO_ID";

    @GetMapping
    public ResponseEntity<List<LivroResponse>> listarTodos(HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        List<LivroResponse> livros = livroService.findAll(usuarioId).stream()
                .map(LivroResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable String id, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        return ResponseEntity.ok(LivroResponse.from(livroService.findById(id, usuarioId)));
    }

    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(@Valid @RequestBody LivroRequest livroRequest, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        Livro livro = livroService.cadastrar(livroRequest.toEntity(), usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(LivroResponse.from(livro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(@PathVariable String id, @Valid @RequestBody LivroRequest livroRequest, HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        Livro livro = livroService.atualizar(id, livroRequest.toEntity(), usuarioId);
        return ResponseEntity.ok(LivroResponse.from(livro));
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
