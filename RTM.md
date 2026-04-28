# Matriz de Rastreabilidade de Requisitos (RTM)

| ID | Requisito Funcional | Classe de Teste | Tipo de Teste | Técnica | Status |
|:---|:---|:---|:---|:---|:---|
| **RF01** | Cadastro de Livros | `LivroRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ✅ Passou |
| **RF02** | Listagem de Livros | `LivroControllerIT` | E2E / Controller | Caixa Preta | ✅ Passou |
| **RF03** | Cadastro de Usuário | `UsuarioRepositoryIT` | Integração (Testcontainers) | Caixa Branca | ✅ Passou |
| **RF04** | Validação de E-mail Único | `UsuarioServiceIT` | Integração (Sem Mock) | Caixa Branca | ✅ Passou |
| **RF05** | Endpoint de Usuários | `UsuarioControllerIT` | E2E / Controller | Caixa Preta | ✅ Passou |
| **RF06** | Autenticação de Usuário | `AuthControllerTest` | E2E / Controller | Caixa Preta | ✅ Passou |
| **RF07** | Busca de ISBN (API Externa) | `ExternalApiVCRTest` | Integração (VCR) | Caixa Preta | ✅ Passou |

---
*Nota: Cobertura de código atingiu a meta de **80%** (medida pelo JaCoCo) através da implementação dos testes de Controller (Caixa Preta) e Services (Caixa Branca), utilizando instâncias reais via Testcontainers.*