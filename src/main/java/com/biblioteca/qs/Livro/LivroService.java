package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {
    private LivroRepository livroRepository;
    public List<Livro> findAll() {
        return livroRepository.findAll();
    }
}
