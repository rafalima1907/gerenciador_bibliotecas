package com.biblioteca.qs.Livro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public List<Livro> findAll(String usuarioId) {
        return livroRepository.findByUsuarioId(usuarioId);
    }

    public Livro findById(String id, String usuarioId) {
        return livroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Livro nao encontrado"));
    }

    public Livro cadastrar(Livro livro, String usuarioId) {
        livro.setUsuarioId(usuarioId);
        prepararParaPersistencia(livro);
        garantirIsbnUnico(livro.getIsbn(), null, usuarioId);
        return livroRepository.save(livro);
    }

    public Livro atualizar(String id, Livro livro, String usuarioId) {
        Livro existente = findById(id, usuarioId);
        
        livro.setUsuarioId(usuarioId);
        prepararParaPersistencia(livro);
        garantirIsbnUnico(livro.getIsbn(), id, usuarioId);

        existente.setTitulo(livro.getTitulo());
        existente.setAutor(livro.getAutor());
        existente.setIsbn(livro.getIsbn());

        return livroRepository.save(existente);
    }

    public void excluir(String id, String usuarioId) {
        Livro existente = findById(id, usuarioId);
        livroRepository.delete(existente);
    }

    private void prepararParaPersistencia(Livro livro) {
        livro.setTitulo(livro.getTitulo().trim());
        livro.setAutor(livro.getAutor().trim());
        livro.setIsbn(normalizarIsbn(livro.getIsbn()));
    }

    private String normalizarIsbn(String isbn) {
        String isbnNormalizado = manterDigitosEXFinal(isbn);

        if (!isbnValido(isbnNormalizado)) {
            throw new IllegalArgumentException("ISBN deve conter 10 ou 13 digitos validos");
        }

        return isbnNormalizado;
    }

    private String manterDigitosEXFinal(String isbn) {
        if (isbn == null) {
            return "";
        }

        StringBuilder normalizado = new StringBuilder();
        for (int i = 0; i < isbn.length(); i++) {
            char caractere = isbn.charAt(i);
            if (Character.isDigit(caractere)) {
                normalizado.append(caractere);
            } else if (normalizado.length() == 9 && (caractere == 'X' || caractere == 'x')) {
                normalizado.append('X');
            }
        }
        return normalizado.toString();
    }

    private boolean isbnValido(String isbn) {
        if (isbn.length() == 13) {
            return contemApenasDigitos(isbn);
        }
        if (isbn.length() == 10) {
            return contemApenasDigitos(isbn.substring(0, 9)) && ehDigitoOuX(isbn.charAt(9));
        }
        return false;
    }

    private boolean contemApenasDigitos(String valor) {
        for (int i = 0; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean ehDigitoOuX(char caractere) {
        return Character.isDigit(caractere) || caractere == 'X';
    }

    private void garantirIsbnUnico(String isbn, String idAtual, String usuarioId) {
        livroRepository.findByIsbnAndUsuarioId(isbn, usuarioId)
                .filter(livro -> idAtual == null || !livro.getId().equals(idAtual))
                .ifPresent(livro -> {
                    throw new IllegalArgumentException("ISBN ja cadastrado");
                });
    }
}