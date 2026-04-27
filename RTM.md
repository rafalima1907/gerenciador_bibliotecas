# Matriz de Rastreabilidade de Requisitos (RTM)

| ID | Requisito Funcional | Classe de Teste | Tipo de Teste | Técnica | Status |
|:---|:---|:---|:---|:---|:---|
| **RF01** | Cadastro de Livros | `LivroRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ✅ Passou |
| **RF02** | Listagem de Livros | `LivroRepositoryIT` | Integração (Testcontainers) | Caixa Preta | ✅ Passou |
| **RF03** | Cadastro de Usuário (Persistência) | `UsuarioRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ✅ Passou (CI) |
| **RF04** | Validação de E-mail Único | `UsuarioServiceTest` | Unitário (Sem Mock) | Caixa Branca | ✅ Passou |
| **RF05** | Autenticação de Usuário | `AuthControllerTest` | E2E / Controller | Caixa Preta | 📅 Pendente |
| **RF06** | Gerenciamento de Sessão | `SessionControllerTest` | E2E / Controller | Caixa Preta | 📅 Pendente |
| **RF07** | Busca de ISBN (API Externa) | `ExternalApiVCRTest` | Integração (VCR) | Caixa Preta | 📅 Pendente |

---
*Nota: A cobertura medida pelo JaCoCo subiu após a inclusão das classes de Usuário. Próximo objetivo: atingir os **80%** com os testes de Controller (Caixa Preta).*