# Gerenciador de Biblioteca Pessoal

Projeto academico da disciplina **Qualidade de Software - SENAC**.

O sistema implementa uma biblioteca pessoal com cadastro/autenticacao de usuarios,
sessao web e CRUD de livros persistido em MongoDB. A entrega foi estruturada para
evidenciar qualidade por meio de regras de negocio explicitas, testes automatizados,
Testcontainers, WireMock/VCR, JaCoCo, GitHub Actions e SonarCloud.

## Escopo Atendido

- Cadastro de usuarios com e-mail unico e preenchimento de endereco por CEP.
- Senha protegida com BCrypt antes da persistencia.
- Login, consulta da sessao atual e logout via `HttpSession`.
- Interface web funcional em `/` para cadastro/login e CRUD de livros.
- CRUD completo de livros em `/api/livros`.
- Validacao de campos obrigatorios e formato de e-mail.
- Regra de negocio para ISBN valido e unico.
- Consulta externa do ViaCEP isolada por WireMock/VCR nos testes.
- Testes de integracao com MongoDB real via Testcontainers.
- Cobertura minima configurada em 80% via JaCoCo.
- Workflow de CI em `.github/workflows/ci.yml`.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web, Validation, Data MongoDB e Security Crypto
- MongoDB
- Maven Wrapper
- JUnit 5
- Testcontainers
- WireMock
- JaCoCo
- GitHub Actions
- SonarCloud

## Como Executar Localmente

Subir MongoDB local:

```bash
docker run --name biblioteca-mongo -p 27017:27017 -d mongo:7
```

Executar a aplicacao:

```bash
./mvnw spring-boot:run
```

A interface fica disponivel em:

```text
http://localhost:8080
```

## Testes e Cobertura

Rodar o ciclo completo da entrega:

```bash
./mvnw clean verify
```

Esse comando executa:

- testes de contexto pelo Surefire;
- testes de integracao `*IT` pelo Failsafe;
- MongoDB real com Testcontainers;
- WireMock para simular a API externa ViaCEP;
- relatorio JaCoCo;
- verificacao de cobertura minima de 80%.

Relatorio local:

```text
target/site/jacoco/index.html
```

Observacao: os testes de integracao exigem Docker ativo. Sem Docker, o `verify`
falha ao iniciar o container `mongo:7`, o que tambem serve como evidencia de que
os testes nao estao usando mock de persistencia.

## Endpoints Principais

Usuarios:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`
- `POST /usuarios`
- `POST /api/usuarios`

Livros:

- `GET /api/livros`
- `GET /api/livros/{id}`
- `POST /api/livros`
- `PUT /api/livros/{id}`
- `DELETE /api/livros/{id}`

## Qualidade Automatizada

O workflow `.github/workflows/ci.yml` roda em `push` e `pull_request`, executando
`./mvnw -B -ntp clean verify`, publicando relatorios de testes/cobertura e
enviando analise ao SonarCloud quando `SONAR_TOKEN` estiver configurado.

## Rastreabilidade

A matriz completa esta em [RTM.md](RTM.md), com requisitos funcionais,
requisitos nao funcionais, testes associados e diagramas UML de sequencia.
