package com.biblioteca.qs.Usuario;

public record UsuarioResponse(
        String id,
        String nome,
        String email,
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCep(),
                usuario.getLogradouro(),
                usuario.getBairro(),
                usuario.getLocalidade(),
                usuario.getUf()
        );
    }
}
