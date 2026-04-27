package com.biblioteca.qs.Usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class UsuarioServiceIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UsuarioService service;

    @Test
    void deveBarrarCadastroDeEmailDuplicado() {
        
        Usuario u1 = new Usuario();
        u1.setNome("José");
        u1.setEmail("teste@senac.com");
        u1.setSenha("senha123");
        service.cadastrar(u1); 

        
        Usuario u2 = new Usuario();
        u2.setNome("Carlos");
        u2.setEmail("teste@senac.com");
        u2.setSenha("outrasenha");

        
        assertThrows(RuntimeException.class, () -> service.cadastrar(u2));
    }
}