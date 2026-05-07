# RTM - Matriz de Rastreabilidade de Requisitos

Projeto: Gerenciador de Biblioteca Pessoal  
Disciplina: Qualidade de Software - SENAC  

## Requisitos Funcionais

| ID | Requisito | Implementacao | Testes |
| --- | --- | --- | --- |
| RF01 | Cadastrar usuario com nome, e-mail e senha. | `UsuarioController`, `AuthController`, `UsuarioService` | `UsuarioControllerIT.deveCadastrarUsuarioViaApi`, `AuthControllerIT.deveRegistrarUsuarioEConsultarSessaoAtual` |
| RF02 | Impedir cadastro de e-mail duplicado. | `UsuarioService.cadastrar` | `UsuarioServiceIT.deveLancarExcecaoParaEmailDuplicado`, `UsuarioControllerIT.deveRetornarErroParaEmailDuplicado` |
| RF03 | Autenticar usuario e manter sessao. | `AuthController`, `UsuarioService.autenticar` | `AuthControllerIT.deveRealizarLoginComCredenciaisValidas`, `AuthControllerIT.deveRegistrarUsuarioEConsultarSessaoAtual` |
| RF04 | Recusar credenciais invalidas. | `UsuarioService.autenticar`, `ApiExceptionHandler` | `UsuarioServiceIT.deveLancarExcecaoParaSenhaIncorreta`, `AuthControllerIT.deveRecusarLoginComCredenciaisInvalidas` |
| RF05 | Cadastrar livro. | `LivroController`, `LivroService.cadastrar` | `LivroControllerIT.deveCadastrarLivroViaEndpoint`, `LivroServiceIT.deveCadastrarEListarLivros` |
| RF06 | Listar livros cadastrados. | `LivroController.listarTodos`, `LivroService.findAll` | `LivroControllerIT.deveListarLivrosViaEndpoint`, `LivroServiceIT.deveCadastrarEListarLivros` |
| RF07 | Atualizar livro existente. | `LivroController.atualizar`, `LivroService.atualizar` | `LivroControllerIT.deveAtualizarLivroViaEndpoint` |
| RF08 | Excluir livro existente. | `LivroController.excluir`, `LivroService.excluir` | `LivroControllerIT.deveExcluirLivroViaEndpoint` |
| RF09 | Impedir ISBN duplicado. | `LivroService.garantirIsbnUnico`, `LivroRepository.findByIsbn` | `LivroServiceIT.deveRejeitarIsbnDuplicado`, `LivroControllerIT.deveRetornarConflitoParaIsbnDuplicado` |
| RF10 | Validar e normalizar ISBN. | `LivroService.normalizarIsbn` | `LivroServiceIT.deveNormalizarIsbnAntesDePersistir`, `LivroServiceIT.deveRejeitarIsbnInvalido` |
| RF11 | Consultar dados externos por ISBN sem depender de internet nos testes. | `IsbnClient`, `LivroService.consultarIsbnExterno` | `ExternalApiVCRIT.deveSimularGravacaoVCR` |
| RF12 | Disponibilizar interface web funcional com gerenciamento de sessao. | `src/main/resources/static/index.html` | Coberto indiretamente pelos endpoints de auth/livros em `AuthControllerIT` e `LivroControllerIT` |

## Requisitos Nao Funcionais

| ID | Requisito | Evidencia |
| --- | --- | --- |
| RNF01 | Usar Spring Boot, MongoDB e arquitetura MVC. | Pacotes `Controller`, `Service`, `Repository` e `@Document` MongoDB |
| RNF02 | Nao usar mocks para persistencia. | Testes `*IT` usam `MongoDBContainer("mongo:7")` |
| RNF03 | Usar VCR/WireMock para chamadas externas. | `ExternalApiVCRIT` usa `@WireMockTest` |
| RNF04 | Cobertura minima de 80%. | Regra `jacoco:check` no `pom.xml` |
| RNF05 | CI em GitHub Actions. | `.github/workflows/ci.yml` |
| RNF06 | Analise SonarCloud/SonarQube. | `sonar-project.properties` e etapa `SonarCloud` no CI |
| RNF07 | Documentacao de rastreabilidade. | Este arquivo `RTM.md` |

## Diagramas de Sequencia

### RF01/RF03 - Cadastro e Sessao

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant A as AuthController
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer
    participant H as HttpSession

    UI->>A: POST /api/auth/register
    A->>S: cadastrar(usuario)
    S->>R: findByEmail(email)
    R->>DB: consulta e-mail
    DB-->>R: vazio
    S->>S: criptografar senha
    S->>R: save(usuario)
    R->>DB: persistir usuario
    DB-->>R: usuario salvo
    S-->>A: usuario salvo
    A->>H: setAttribute(USUARIO_ID)
    A-->>UI: 201 UsuarioResponse
```

### RF02 - E-mail Duplicado

```mermaid
sequenceDiagram
    participant T as Teste Caixa Branca
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer

    T->>S: cadastrar(usuario com e-mail repetido)
    S->>R: findByEmail(email)
    R->>DB: consulta e-mail
    DB-->>R: usuario encontrado
    S-->>T: IllegalArgumentException
```

### RF05/RF09/RF10 - Cadastro de Livro

```mermaid
sequenceDiagram
    participant C as LivroController
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    C->>S: cadastrar(livro)
    S->>S: validar campos e normalizar ISBN
    S->>R: findByIsbn(isbn)
    R->>DB: consulta ISBN
    DB-->>R: vazio
    S->>R: save(livro)
    R->>DB: persistir livro
    DB-->>R: livro salvo
    R-->>S: livro salvo
    S-->>C: livro salvo
```

### RF07/RF08 - Atualizacao e Exclusao de Livro

```mermaid
sequenceDiagram
    participant C as LivroController
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    C->>S: atualizar(id, livro)
    S->>R: findById(id)
    R->>DB: buscar livro
    DB-->>R: livro existente
    S->>S: validar dados novos
    S->>R: save(livro atualizado)
    R->>DB: atualizar documento
    C->>S: excluir(id)
    S->>R: findById(id)
    S->>R: delete(livro)
    R->>DB: remover documento
```

### RF11 - Consulta Externa com WireMock/VCR

```mermaid
sequenceDiagram
    participant T as ExternalApiVCRIT
    participant S as LivroService
    participant I as IsbnClient
    participant W as WireMock

    T->>W: stub GET /api/books/{isbn}
    T->>S: consultarIsbnExterno(isbn)
    S->>I: buscarDadosPorIsbn(isbn)
    I->>W: GET /api/books/{isbn}
    W-->>I: JSON gravado
    I-->>S: resposta externa simulada
    S-->>T: dados do livro
```

## Criterios de Aceite da Entrega

- `./mvnw clean verify` deve passar com Docker ativo.
- GitHub Actions deve ficar verde em `push` e `pull_request`.
- Relatorio JaCoCo deve apresentar cobertura minima de 80%.
- SonarCloud deve executar quando `SONAR_TOKEN` estiver configurado.
- Nenhum segredo deve ser versionado em cassetes, propriedades ou workflow.
