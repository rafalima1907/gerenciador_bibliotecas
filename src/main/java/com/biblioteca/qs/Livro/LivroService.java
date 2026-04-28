package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {
    
    private final LivroRepository livroRepository;
    private final IsbnClient isbnClient; // Injeção do cliente externo

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Livro cadastrar(Livro livro) {
        return livroRepository.save(livro);
    }

    // Método solicitado para o requisito RF07
    public String consultarIsbnExterno(String isbn) {
        return isbnClient.buscarDadosPorIsbn(isbn);
    }
}