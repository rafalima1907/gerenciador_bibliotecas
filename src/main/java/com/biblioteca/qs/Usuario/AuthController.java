package com.biblioteca.qs.Usuario;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    static final String USUARIO_ID_SESSION_KEY = "USUARIO_ID";

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody Usuario usuario, HttpSession session) {
        Usuario salvo = usuarioService.cadastrar(usuario);
        session.setAttribute(USUARIO_ID_SESSION_KEY, salvo.getId());
        return UsuarioResponse.from(salvo);
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Usuario usuario = usuarioService.autenticar(request.email(), request.senha());
        session.setAttribute(USUARIO_ID_SESSION_KEY, usuario.getId());
        return UsuarioResponse.from(usuario);
    }

    @GetMapping("/me")
    public UsuarioResponse usuarioAtual(HttpSession session) {
        Object usuarioId = session.getAttribute(USUARIO_ID_SESSION_KEY);
        if (usuarioId == null) {
            throw new IllegalArgumentException("Sessao nao autenticada");
        }

        return UsuarioResponse.from(usuarioService.buscarPorId(usuarioId.toString()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
