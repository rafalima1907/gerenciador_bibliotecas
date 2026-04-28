## 📊 Diagramas de Sequência UML

### 1. Cadastro com Persistência Real (RF01/RF03)
*Este diagrama representa os testes que utilizam Testcontainers para validar o banco de dados.*

```mermaid
sequenceDiagram
    participant T as Teste (IT)
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant DB as MongoDB (Container)

    T->>C: POST /endpoint (JSON)
    C->>S: cadastrar()
    S->>R: save()
    R->>DB: Persistência no Docker
    DB-->>R: Sucesso
    R-->>S: Objeto Salvo
    S-->>C: Objeto Salvo
    C-->>T: 201 Created
2. Validação de Regra de Negócio (RF04 - Caixa Branca)
Este diagrama mostra a lógica do UsuarioServiceIT barrando e-mails duplicados.

Snippet de código
sequenceDiagram
    participant T as Teste (Caixa Branca)
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB

    T->>S: cadastrar(email_repetido)
    S->>R: findByEmail(email)
    R->>DB: Busca registro
    DB-->>R: Encontrado!
    R-->>S: Retorna Usuário
    Note over S: Lógica de Exceção
    S-->>T: throw RuntimeException ("E-mail já cadastrado")
    
3. Integração com API Externa via VCR (RF07)
Mostra como o WireMock intercepta a chamada de ISBN.

Snippet de código
sequenceDiagram
    participant T as Teste (ExternalApiVCRIT)
    participant S as LivroService
    participant V as WireMock (VCR/Stub)
    
    T->>S: consultarIsbn("97885")
    Note over S, V: Interceptação VCR
    S->>V: GET /api/books/isbn
    V-->>S: Retorna JSON gravado (Mock)
    S-->>T: Dados do Livro