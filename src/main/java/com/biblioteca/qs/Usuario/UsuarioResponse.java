package com.biblioteca.qs.Usuario;

public record UsuarioResponse(String id, String nome, String email) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
