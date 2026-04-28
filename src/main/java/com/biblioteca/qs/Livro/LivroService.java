package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {
    
    private final LivroRepository livroRepository;
    private final IsbnClient isbnClient; 

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Livro cadastrar(Livro livro) {
        return livroRepository.save(livro);
    }

    
    public String consultarIsbnExterno(String isbn) {
        try {
            return isbnClient.buscarDadosPorIsbn(isbn);
        } catch (Exception e) {
            return "Erro ao consultar ISBN: " + e.getMessage();
        }
    }
}