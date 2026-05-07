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

    public Livro findById(String id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro nao encontrado"));
    }

    public Livro cadastrar(Livro livro) {
        prepararParaPersistencia(livro);
        garantirIsbnUnico(livro.getIsbn(), null);
        return livroRepository.save(livro);
    }

    public Livro atualizar(String id, Livro livro) {
        Livro existente = findById(id);
        prepararParaPersistencia(livro);
        garantirIsbnUnico(livro.getIsbn(), id);

        existente.setTitulo(livro.getTitulo());
        existente.setAutor(livro.getAutor());
        existente.setIsbn(livro.getIsbn());

        return livroRepository.save(existente);
    }

    public void excluir(String id) {
        Livro existente = findById(id);
        livroRepository.delete(existente);
    }

    public String consultarIsbnExterno(String isbn) {
        try {
            return isbnClient.buscarDadosPorIsbn(normalizarIsbn(isbn));
        } catch (Exception e) {
            return "Erro ao consultar ISBN: " + e.getMessage();
        }
    }

    private void prepararParaPersistencia(Livro livro) {
        livro.setTitulo(livro.getTitulo().trim());
        livro.setAutor(livro.getAutor().trim());
        livro.setIsbn(normalizarIsbn(livro.getIsbn()));
    }

    private String normalizarIsbn(String isbn) {
        String isbnNormalizado = isbn == null ? "" : isbn.replaceAll("[^0-9Xx]", "").toUpperCase();

        if (!isbnNormalizado.matches("[0-9]{10}|[0-9]{13}|[0-9]{9}X")) {
            throw new IllegalArgumentException("ISBN deve conter 10 ou 13 digitos validos");
        }

        return isbnNormalizado;
    }

    private void garantirIsbnUnico(String isbn, String idAtual) {
        livroRepository.findByIsbn(isbn)
                .filter(livro -> idAtual == null || !livro.getId().equals(idAtual))
                .ifPresent(livro -> {
                    throw new IllegalArgumentException("ISBN ja cadastrado");
                });
    }
}
