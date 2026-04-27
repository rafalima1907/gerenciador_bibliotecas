# Matriz de Rastreabilidade de Requisitos (RTM)

| ID | Requisito Funcional | Classe de Teste | Tipo de Teste | Técnica | Status |
|:---|:---|:---|:---|:---|:---|
| **RF01** | Cadastro de Livros | `LivroRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ✅ Passou |
| **RF02** | Listagem de Livros | `LivroRepositoryIT` | Integração (Testcontainers) | Caixa Preta | ✅ Passou |
| **RF03** | Cadastro de Usuário | `UsuarioRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ⏳ Em Desenvolvimento |
| **RF04** | Autenticação de Usuário | `AuthControllerTest` | E2E / Controller | Caixa Preta | 📅 Pendente |
| **RF05** | Gerenciamento de Sessão | `SessionControllerTest` | E2E / Controller | Caixa Preta | 📅 Pendente |
| **RF06** | Busca de ISBN (API Externa) | `ExternalApiVCRTest` | Integração (VCR) | Caixa Preta | 📅 Pendente |

---
*Nota: A cobertura atual medida pelo JaCoCo é de **63%**. A meta de **80%** será atingida com a implementação dos testes de Integração de Usuário (RF03).*