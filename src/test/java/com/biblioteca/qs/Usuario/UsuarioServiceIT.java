package com.biblioteca.qs.Usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class UsuarioServiceIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private UsuarioService service;

    @Test
    void deveBarrarCadastroDeEmailDuplicado() {
        Usuario u1 = new Usuario();
        u1.setNome("José");
        u1.setEmail("teste@senac.com");
        service.cadastrar(u1); 

        Usuario u2 = new Usuario();
        u2.setNome("Carlos");
        u2.setEmail("teste@senac.com");

        
        assertThrows(RuntimeException.class, () -> service.cadastrar(u2));
    }
}