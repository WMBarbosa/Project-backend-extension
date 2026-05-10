# Raízes do Nordeste — Back-end API

Sistema de gerenciamento para a rede de lanchonetes **Raízes do Nordeste**.  
Projeto Multidisciplinar — Trilha Back-End — UNINTER 2026.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Segurança | Spring Security + JWT (jjwt 0.12.5) |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL 15+ |
| Migrations | Flyway |
| Documentação API | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven 3.9+ |
| Testes | JUnit 5 + Mockito |

---

## Pré-requisitos

- Java 21 ([download](https://adoptium.net/))
- Maven 3.9+ (`mvn -v` para verificar)
- PostgreSQL 15+ rodando localmente
- (Opcional) Docker para subir o banco

---

## 1. Configurar variáveis de ambiente

```bash
cp .env.example .env
# Edite o .env com suas credenciais do banco
```

Variáveis necessárias:

```
DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
JWT_SECRET=chave-secreta-longa
JWT_EXPIRATION=86400000
```

---

## 2. Criar banco de dados (PostgreSQL)

```sql
CREATE DATABASE raizes_nordeste;
```

Ou com Docker:

```bash
docker run --name raizes-pg \
  -e POSTGRES_DB=raizes_nordeste \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:15
```

---

## 3. Instalar dependências

```bash
mvn clean install -DskipTests
```

---

## 4. Executar migrations e seed

As migrations são executadas **automaticamente** pelo Flyway ao iniciar a aplicação.

- `V1__create_initial_schema.sql` — cria todas as tabelas
- `V2__seed_initial_data.sql` — popula dados iniciais de teste

---

## 5. Iniciar a API

```bash
# Exportando variáveis de ambiente (Linux/macOS)
export $(cat .env | xargs)
mvn spring-boot:run

# Ou passando diretamente:
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="-DDB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste -DDB_USERNAME=postgres -DDB_PASSWORD=postgres"
```

A API inicia em: **http://localhost:8080**

---

## 6. Acessar a documentação (Swagger/OpenAPI)

```
http://localhost:8080/swagger-ui.html
```

JSON OpenAPI:
```
http://localhost:8080/api-docs
```

---

## 7. Rodar os testes

```bash
mvn test
```

Os testes usam H2 em memória — **não precisam do PostgreSQL**.

---

## 8. Usuários do seed (para testes)

| E-mail | Senha | Perfil |
|---|---|---|
| admin@raizesnordeste.com | password | ADMIN |
| gerente@raizesnordeste.com | password | GERENTE |
| atendente@raizesnordeste.com | password | ATENDENTE |
| cozinha@raizesnordeste.com | password | COZINHA |
| cliente@raizesnordeste.com | password | CLIENTE |

> Senha do seed: `password` (BCrypt hash do Laravel padrão usado no seed).  
> Para testes reais, cadastre usuários via `POST /api/v1/auth/cadastro`.

---

## Estrutura do projeto

```
src/main/java/com/raizes/
├── RaizesDoNordesteApplication.java
├── domain/
│   ├── entity/          # Entidades JPA (Pedido, Produto, Usuario…)
│   ├── enums/           # Enums de domínio
│   └── exception/       # Exceções de domínio
├── application/
│   ├── service/         # Casos de uso / serviços
│   └── dto/             # Request e Response DTOs
├── infrastructure/
│   ├── config/          # SecurityConfig, OpenApiConfig, AuditService
│   ├── security/        # JwtService, JwtAuthenticationFilter
│   ├── integration/     # GatewayPagamentoMock
│   └── persistence/     # Repositórios JPA
└── api/
    ├── controller/      # Controllers REST
    └── handler/         # GlobalExceptionHandler
```

---

## Principais endpoints

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Login | Público |
| POST | `/api/v1/auth/cadastro` | Cadastro | Público |
| GET | `/api/v1/produtos` | Listar cardápio | Público |
| GET | `/api/v1/unidades` | Listar unidades | Público |
| POST | `/api/v1/pedidos` | Criar pedido | CLIENTE/ATENDENTE |
| GET | `/api/v1/pedidos?canalPedido=TOTEM` | Filtrar por canal | ADMIN/GERENTE |
| PATCH | `/api/v1/pedidos/{id}/status` | Atualizar status | GERENTE/COZINHA |
| POST | `/api/v1/pagamentos/pedidos/{id}/processar` | Processar pagamento mock | CLIENTE |
| GET | `/api/v1/estoque/unidades/{id}` | Consultar estoque | GERENTE |
| POST | `/api/v1/estoque/unidades/{id}/movimentar` | Movimentar estoque | GERENTE |
| GET | `/api/v1/fidelidade/usuarios/{id}/saldo` | Saldo de pontos | CLIENTE |

> Veja todos os endpoints com exemplos completos no Swagger UI.

---

## Fluxo crítico — Pedido → Pagamento Mock → Status

```
1. POST /api/v1/auth/login              → obtém token JWT
2. POST /api/v1/pedidos                 → cria pedido (canalPedido obrigatório)
3. POST /api/v1/pagamentos/pedidos/{id}/processar → processa pagamento mock
   → valor normal: APROVADO → status = PAGAMENTO_APROVADO
   → valor .99:   RECUSADO → status = CANCELADO
4. PATCH /api/v1/pedidos/{id}/status    → avança: EM_PREPARO → PRONTO → ENTREGUE
```

---

## LGPD — Controles implementados

- Senhas armazenadas com **BCrypt** (nunca em texto puro)
- JWT sem dados sensíveis no payload
- Consentimento explícito para programa de fidelidade
- Logs de auditoria (`audit_logs`) para ações sensíveis
- Respostas da API não expõem senha ou dados desnecessários
- Perfis/roles controlam acesso a dados pessoais

---

## Coleção Postman

Importe o arquivo `raizes-nordeste-postman.json` no repositório.  
Configure a variável de ambiente `base_url = http://localhost:8080` e `token` com o JWT do login.

---

## Regra de simulação do pagamento mock

| Valor do pedido | Resultado |
|---|---|
| Termina em `.99` (ex: R$ 50,99) | **RECUSADO** |
| Qualquer outro valor | **APROVADO** |
