package com.biblioteca.qs.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ViaCepClient viaCepClient;

    public Usuario cadastrar(Usuario usuario) {
        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(normalizarEmail(usuario.getEmail()));
        preencherEnderecoQuandoCepInformado(usuario);

        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = repository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais invalidas");
        }

        return usuario;
    }

    public Usuario buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void preencherEnderecoQuandoCepInformado(Usuario usuario) {
        if (usuario.getCep() == null || usuario.getCep().isBlank()) {
            return;
        }

        ViaCepResponse endereco = viaCepClient.buscarEnderecoPorCep(usuario.getCep());
        usuario.setCep(endereco.cep());
        usuario.setLogradouro(endereco.logradouro());
        usuario.setBairro(endereco.bairro());
        usuario.setLocalidade(endereco.localidade());
        usuario.setUf(endereco.uf());
    }
}
