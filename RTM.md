# RTM - Matriz de Rastreabilidade de Requisitos

Projeto: Gerenciador de Biblioteca Pessoal  
Disciplina: Qualidade de Software - SENAC

## Requisitos Funcionais

| ID | Requisito | Regra de negocio | Implementacao | Testes |
| --- | --- | --- | --- | --- |
| RF01 | Cadastrar usuario. | O sistema deve cadastrar usuario com nome, e-mail e senha; quando CEP for informado, deve preencher o endereco por API externa controlada por VCR/WireMock. | `UsuarioController`, `AuthController`, `UsuarioService.cadastrar`, `ViaCepClient` | `UsuarioControllerIT.deveCadastrarUsuarioViaApi`, `AuthControllerIT.deveRegistrarUsuarioEConsultarSessaoAtual`, `UsuarioServiceIT.deveCadastrarUsuarioComSucessoECriptografarSenha`, `ExternalApiVCRIT.deveSimularConsultaViaCepNoCadastroUsuario` |
| RF02 | Impedir e-mail duplicado. | Nao deve existir mais de um usuario com o mesmo e-mail normalizado. | `UsuarioService.cadastrar`, `UsuarioRepository.findByEmail` | `UsuarioServiceIT.deveLancarExcecaoParaEmailDuplicado`, `UsuarioControllerIT.deveRetornarErroParaEmailDuplicado` |
| RF03 | Autenticar usuario e manter sessao. | Credenciais validas devem iniciar sessao HTTP e permitir consulta do usuario atual. | `AuthController`, `UsuarioService.autenticar`, `HttpSession` | `AuthControllerIT.deveRealizarLoginComCredenciaisValidas`, `AuthControllerIT.deveRegistrarUsuarioEConsultarSessaoAtual`, `UsuarioServiceIT.deveAutenticarUsuarioComSenhaCorreta`, `UsuarioServiceIT.deveAutenticarNormalizandoEmailComEspacosEMaiusculas` |
| RF04 | Recusar credenciais invalidas. | Login com senha ou usuario invalidos deve ser recusado com erro de regra de negocio. | `UsuarioService.autenticar`, `ApiExceptionHandler` | `UsuarioServiceIT.deveLancarExcecaoParaSenhaIncorreta`, `AuthControllerIT.deveRecusarLoginComCredenciaisInvalidas` |
| RF05 | Cadastrar livro para usuario autenticado. | O livro deve ser salvo vinculado ao usuario da sessao. Requisicoes sem sessao devem ser recusadas. A API recebe `LivroRequest` e devolve `LivroResponse`, sem expor diretamente a entidade persistente. | `LivroController.cadastrar`, `LivroRequest`, `LivroResponse`, `LivroService.cadastrar` | `LivroControllerIT.deveCadastrarLivroViaEndpoint`, `LivroControllerIT.deveRecusarRequisicaoSemSessao`, `LivroServiceIT.deveCadastrarEListarLivrosDoUsuario` |
| RF06 | Listar livros do usuario autenticado. | A listagem deve retornar apenas os livros pertencentes ao usuario da sessao usando DTO de resposta. | `LivroController.listarTodos`, `LivroResponse`, `LivroService.findAll`, `LivroRepository.findByUsuarioId` | `LivroControllerIT.deveListarLivrosViaEndpoint`, `LivroServiceIT.deveCadastrarEListarLivrosDoUsuario` |
| RF07 | Atualizar livro do usuario autenticado. | Somente livro pertencente ao usuario da sessao pode ser atualizado; os novos dados devem ser recebidos por DTO e validados antes da persistencia. | `LivroController.atualizar`, `LivroRequest`, `LivroResponse`, `LivroService.atualizar`, `LivroRepository.findByIdAndUsuarioId` | `LivroControllerIT.deveAtualizarLivroViaEndpoint`, `LivroServiceIT.deveAtualizarLivroMantendoMesmoIsbn`, `LivroServiceIT.deveRejeitarAtualizacaoComIsbnDeOutroLivroDoMesmoUsuario` |
| RF08 | Excluir livro do usuario autenticado. | Somente livro pertencente ao usuario da sessao pode ser excluido. | `LivroController.excluir`, `LivroService.excluir`, `LivroRepository.findByIdAndUsuarioId` | `LivroControllerIT.deveExcluirLivroViaEndpoint` |
| RF09 | Impedir ISBN duplicado para o mesmo usuario. | O mesmo usuario nao pode cadastrar dois livros com o mesmo ISBN normalizado; usuarios diferentes podem ter o mesmo ISBN em suas bibliotecas pessoais. | `LivroService.garantirIsbnUnico`, `LivroRepository.findByIsbnAndUsuarioId` | `LivroServiceIT.deveRejeitarIsbnDuplicadoParaOMesmoUsuario`, `LivroServiceIT.devePermitirMesmoIsbnParaUsuariosDiferentes`, `LivroControllerIT.deveRetornarConflitoParaIsbnDuplicado`, `LivroRepositoryIT.deveBuscarLivroPorIsbnEUsuarioId` |
| RF10 | Validar e normalizar ISBN. | ISBN deve ser normalizado, aceitando ISBN-10 ou ISBN-13 validos, e recusando valores nulos, vazios ou invalidos. | `LivroService.normalizarIsbn` | `LivroServiceIT.deveNormalizarIsbnAntesDePersistir`, `LivroServiceIT.deveAceitarIsbn10ComXFinal`, `LivroServiceIT.deveAceitarIsbn10ApenasNumerico`, `LivroServiceIT.deveRejeitarIsbnInvalido`, `LivroServiceIT.deveRejeitarIsbnNuloOuVazio` |
| RF11 | Consultar API externa com VCR/WireMock. | Chamadas ao ViaCEP devem ser reproduzidas em teste sem internet real, cobrindo sucesso, CEP invalido, CEP nao encontrado e indisponibilidade externa. | `ViaCepClient`, `UsuarioService.preencherEnderecoQuandoCepInformado` | `ExternalApiVCRIT.deveSimularConsultaViaCepNoCadastroUsuario`, `ExternalApiVCRIT.deveRejeitarCepComFormatoInvalidoAntesDaConsultaExterna`, `ExternalApiVCRIT.deveRetornarErroQuandoViaCepInformarCepNaoEncontrado`, `ExternalApiVCRIT.deveRetornarErroQuandoViaCepEstiverIndisponivel`, `UsuarioControllerIT.deveConsultarEnderecoPorCepViaApi` |
| RF12 | Disponibilizar interface web funcional com sessao. | A aplicacao deve servir uma interface web para cadastro/login e operacoes de livros usando a sessao do usuario autenticado. | `src/main/resources/static/index.html`, endpoints `/api/auth` e `/api/livros` | `FrontendIT.deveServirInterfaceWeb`, `AuthControllerIT.deveRegistrarUsuarioEConsultarSessaoAtual`, `LivroControllerIT.deveCadastrarLivroViaEndpoint`, `LivroControllerIT.deveListarLivrosViaEndpoint` |

## Requisitos Nao Funcionais

| ID | Requisito | Evidencia |
| --- | --- | --- |
| RNF01 | Usar Spring Boot, MongoDB e arquitetura MVC. | Camadas `Controller`, `Service`, `Repository`, entidades MongoDB com `@Document` e aplicacao Spring Boot. |
| RNF02 | Proibido usar mocks no projeto final. | A busca por `Mockito`, `@Mock`, `@MockBean`, `mock()` e `when()` nao encontrou uso de mocks. A dependencia transitiva de Mockito foi excluida do `spring-boot-starter-test`. Persistencia e testada com `MongoDBContainer("mongo:7")`. A unica simulacao permitida e a API externa via WireMock/VCR. |
| RNF03 | Usar Testcontainers para persistencia. | Testes `*IT` de usuarios, livros e repositorios usam `@Testcontainers`, `@Container`, `@ServiceConnection` e `MongoDBContainer("mongo:7")`. |
| RNF04 | Usar VCR para chamadas externas. | `ExternalApiVCRIT` e `UsuarioControllerIT` usam `@WireMockTest` para reproduzir respostas do ViaCEP sem depender de internet. |
| RNF05 | Cobertura minima de 80%. | Regra `jacoco:check` no `pom.xml`, validada no comando `./mvnw clean verify`. |
| RNF06 | CI em GitHub Actions. | Workflow `.github/workflows/ci.yml` executa build, testes, cobertura e analise de qualidade. |
| RNF07 | Analise SonarCloud/SonarQube. | `sonar-project.properties` e etapa `SonarCloud` no CI, executada quando `SONAR_TOKEN` estiver configurado. |
| RNF08 | Rastreabilidade completa. | Este `RTM.md` mapeia cada requisito funcional aos testes e a um diagrama UML de sequencia individual. |

## Verificacao de Mocks

O enunciado proibe mocks no projeto final. A verificacao local buscou os principais sinais de mocks em codigo e testes:

```powershell
rg -n "mock|Mock|Mockito|@Mock|@MockBean|mockito|when\(|verify\(" src pom.xml
```

Resultado considerado conforme:

- Nao ha dependencia Mockito declarada manualmente no `pom.xml`; `mockito-core` e `mockito-junit-jupiter` estao excluidos do `spring-boot-starter-test`.
- Nao ha `@Mock`, `@MockBean`, `Mockito`, `mock()`, `when()` ou `verify()` aplicados a regras de negocio ou persistencia.
- A persistencia usa MongoDB real em container nos testes de integracao.
- WireMock aparece apenas como VCR/simulacao permitida para a API externa ViaCEP.

## Diagramas de Sequencia

### RF01 - Cadastrar Usuario

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant A as AuthController
    participant S as UsuarioService
    participant V as ViaCepClient
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer
    participant H as HttpSession

    UI->>A: POST /api/auth/register
    A->>S: cadastrar(usuario)
    alt CEP informado
        S->>V: buscarEnderecoPorCep(cep)
        V-->>S: endereco normalizado
    end
    S->>R: findByEmail(email normalizado)
    R->>DB: consultar e-mail
    DB-->>R: vazio
    S->>S: criptografar senha
    S->>R: save(usuario)
    R->>DB: persistir usuario
    DB-->>R: usuario salvo
    S-->>A: usuario salvo
    A->>H: setAttribute(USUARIO_ID)
    A-->>UI: 201 UsuarioResponse
```

### RF02 - Impedir E-mail Duplicado

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant A as AuthController
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer
    participant E as ApiExceptionHandler

    UI->>A: POST /api/auth/register
    A->>S: cadastrar(usuario)
    S->>R: findByEmail(email normalizado)
    R->>DB: consultar e-mail
    DB-->>R: usuario existente
    S-->>A: IllegalArgumentException
    A->>E: tratar excecao
    E-->>UI: 409 E-mail ja cadastrado
```

### RF03 - Autenticar Usuario e Manter Sessao

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant A as AuthController
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer
    participant H as HttpSession

    UI->>A: POST /api/auth/login
    A->>S: autenticar(credenciais)
    S->>R: findByEmail(email normalizado)
    R->>DB: buscar usuario
    DB-->>R: usuario encontrado
    S->>S: validar senha criptografada
    S-->>A: usuario autenticado
    A->>H: setAttribute(USUARIO_ID)
    A-->>UI: 200 UsuarioResponse + cookie de sessao
    UI->>A: GET /api/auth/me
    A->>H: getAttribute(USUARIO_ID)
    A-->>UI: usuario da sessao
```

### RF04 - Recusar Credenciais Invalidas

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant A as AuthController
    participant S as UsuarioService
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer
    participant E as ApiExceptionHandler

    UI->>A: POST /api/auth/login
    A->>S: autenticar(credenciais invalidas)
    S->>R: findByEmail(email normalizado)
    R->>DB: buscar usuario
    DB-->>R: usuario encontrado
    S->>S: comparar senha
    S-->>A: IllegalArgumentException
    A->>E: tratar excecao
    E-->>UI: 409 Credenciais invalidas
```

### RF05 - Cadastrar Livro para Usuario Autenticado

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    UI->>C: POST /api/livros com cookie de sessao
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: cadastrar(livro, usuarioId)
    S->>S: vincular usuarioId e normalizar ISBN
    S->>R: findByIsbnAndUsuarioId(isbn, usuarioId)
    R->>DB: consultar duplicidade do usuario
    DB-->>R: vazio
    S->>R: save(livro)
    R->>DB: persistir livro
    DB-->>R: livro salvo
    S-->>C: livro salvo
    C-->>UI: 201 Livro
```

### RF06 - Listar Livros do Usuario Autenticado

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    UI->>C: GET /api/livros com cookie de sessao
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: findAll(usuarioId)
    S->>R: findByUsuarioId(usuarioId)
    R->>DB: buscar livros do usuario
    DB-->>R: livros encontrados
    R-->>S: lista filtrada por usuario
    S-->>C: lista de livros
    C-->>UI: 200 List<Livro>
```

### RF07 - Atualizar Livro do Usuario Autenticado

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    UI->>C: PUT /api/livros/{id} com cookie de sessao
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: atualizar(id, livro, usuarioId)
    S->>R: findByIdAndUsuarioId(id, usuarioId)
    R->>DB: buscar livro do usuario
    DB-->>R: livro existente
    S->>S: validar dados e normalizar ISBN
    S->>R: findByIsbnAndUsuarioId(isbn, usuarioId)
    R->>DB: verificar duplicidade
    DB-->>R: sem conflito
    S->>R: save(livro atualizado)
    R->>DB: atualizar documento
    DB-->>R: livro atualizado
    S-->>C: livro atualizado
    C-->>UI: 200 Livro
```

### RF08 - Excluir Livro do Usuario Autenticado

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    UI->>C: DELETE /api/livros/{id} com cookie de sessao
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: excluir(id, usuarioId)
    S->>R: findByIdAndUsuarioId(id, usuarioId)
    R->>DB: buscar livro do usuario
    DB-->>R: livro existente
    S->>R: delete(livro)
    R->>DB: remover documento
    C-->>UI: 204 No Content
```

### RF09 - Impedir ISBN Duplicado para o Mesmo Usuario

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer
    participant E as ApiExceptionHandler

    UI->>C: POST /api/livros com ISBN ja usado
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: cadastrar(livro, usuarioId)
    S->>S: normalizar ISBN
    S->>R: findByIsbnAndUsuarioId(isbn, usuarioId)
    R->>DB: consultar ISBN do usuario
    DB-->>R: livro existente
    S-->>C: IllegalArgumentException
    C->>E: tratar excecao
    E-->>UI: 409 ISBN ja cadastrado
```

### RF10 - Validar e Normalizar ISBN

```mermaid
sequenceDiagram
    participant UI as Interface Web
    participant C as LivroController
    participant S as LivroService
    participant R as LivroRepository
    participant DB as MongoDB Testcontainer

    UI->>C: POST /api/livros com ISBN formatado
    C->>S: cadastrar(livro, usuarioId)
    S->>S: remover separadores e manter X final quando ISBN-10
    alt ISBN valido
        S->>R: save(livro com ISBN normalizado)
        R->>DB: persistir livro
        DB-->>R: livro salvo
        S-->>C: livro salvo
        C-->>UI: 201 Livro
    else ISBN invalido
        S-->>C: IllegalArgumentException
        C-->>UI: 400/409 erro de validacao
    end
```

### RF11 - Consultar API Externa com VCR/WireMock

```mermaid
sequenceDiagram
    participant T as ExternalApiVCRIT
    participant S as UsuarioService
    participant V as ViaCepClient
    participant W as WireMock
    participant R as UsuarioRepository
    participant DB as MongoDB Testcontainer

    T->>W: stub GET /ws/{cep}/json/
    T->>S: cadastrar(usuario com CEP)
    S->>V: buscarEnderecoPorCep(cep)
    V->>W: GET /ws/{cep}/json/
    W-->>V: resposta gravada do ViaCEP
    V-->>S: endereco ou erro controlado
    alt endereco valido
        S->>R: save(usuario com endereco)
        R->>DB: persistir usuario
        DB-->>R: usuario salvo
        S-->>T: usuario salvo com endereco
    else CEP invalido, nao encontrado ou indisponivel
        S-->>T: excecao de regra de negocio
    end
```

### RF12 - Interface Web Funcional com Sessao

```mermaid
sequenceDiagram
    participant U as Usuario
    participant UI as Interface Web
    participant A as AuthController
    participant C as LivroController
    participant H as HttpSession
    participant S as LivroService

    U->>UI: acessar /
    UI-->>U: index.html Biblioteca Pessoal
    U->>UI: informar cadastro ou login
    UI->>A: POST /api/auth/register ou /api/auth/login
    A->>H: setAttribute(USUARIO_ID)
    A-->>UI: cookie de sessao
    U->>UI: cadastrar/listar livros
    UI->>C: requisicoes /api/livros com cookie
    C->>H: getAttribute(USUARIO_ID)
    H-->>C: usuarioId
    C->>S: executar regra de livros do usuario
    S-->>C: resultado
    C-->>UI: resposta JSON
    UI-->>U: tela atualizada
```

## Criterios de Aceite da Entrega

- `./mvnw clean verify` deve passar com Docker ativo.
- GitHub Actions deve ficar verde em `push` e `pull_request`.
- Relatorio JaCoCo deve apresentar cobertura minima de 80%.
- SonarCloud deve executar quando `SONAR_TOKEN` estiver configurado.
- Nenhum segredo deve ser versionado em cassetes, propriedades ou workflow.
- Nao deve haver mocks em regras de negocio ou persistencia; persistencia deve usar Testcontainers e chamadas externas devem usar VCR/WireMock.
