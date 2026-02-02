# Sistema de Gerenciamento Musical

Aplicação fullstack para gerenciamento de artistas e álbuns.

```
├── backend/   → API REST com Quarkus (Java 21)
├── frontend/  → Aplicação Angular 19 + Tailwind CSS
└── docker-compose.yml
```

---

## 👤 Feito por

| Campo | Dados |
|-------|-------|
| **Nome** | Elandro Soares Magalhães |
| **CPF** | 053.780.761-61 |
| **Contato** | (65) 99323-6344 |
| **Email** | elandro10@outlook.com |
| **Inscrição** | 16514 |
| **Processo Seletivo** | SEPLAG 001/2026/SEPLAG |
| **Vaga** | Engenheiro da Computação Sênior |

---

## 🚀 Como Executar

### Pré-requisitos
- Docker Engine 19.03+
- Docker Compose V2
- Node.js 22+ (para o frontend)

### Rodar o Backend
```bash
docker compose up --build
```
> ⏳ Na primeira execução o build pode demorar alguns minutos para baixar as dependências Maven e imagens Docker.

### Rodar o Frontend
```bash
cd frontend && npm install && npm start
```
Acesso: http://localhost:4200

### Rodar os Testes Unitários (Backend)
Os testes utilizam Testcontainers e precisam ser executados localmente:
```bash
cd backend && ./mvnw test
```
Requisitos: Java 21 e Maven 3.9+

---

## 🌐 Acessos e Credenciais

| Serviço | URL / Host | Porta | Usuário | Senha |
|---------|------------|-------|---------|-------|
| **Frontend** | `http://localhost:4200` | 4200 | - | - |
| **API** | `http://localhost:8090` | 8090 | - | - |
| **Swagger UI** | [`/swagger-ui`](http://localhost:8090/swagger-ui) | 8090 | - | - |
| **Liveness Probe** | [`/q/health/live`](http://localhost:8090/q/health/live) | 8090 | - | - |
| **Readiness Probe** | [`/q/health/ready`](http://localhost:8090/q/health/ready) | 8090 | - | - |
| **PostgreSQL** | `localhost` | 5444 | `appuser` | `app123` |
| **MinIO API** | `http://localhost:19000` | 19000 | `minioadmin` | `minioadmin` |
| **MinIO Console** | `http://localhost:19001` | 19001 | `minioadmin` | `minioadmin` |

---

## 🏗️ Arquitetura e Decisões Técnicas

O projeto segue **Arquitetura em Camadas (Layered Architecture)**, com separação clara de responsabilidades.

- **Camadas:**
  - `core`: Configurações globais, segurança (JWT), filtros e utilitários.
  - `modules`: Divide o domínio em funcionalidades (Artistas, Álbuns, Regionais).
    - `controllers`: Camada de entrada (REST).
    - `services`: Regras de negócio (Service Layer).
    - `repositories`: Acesso a dados (Repository Pattern com Panache).
    - `entities`: Modelo de dados.
    - `mappers`: Conversão entre DTOs e Entidades (Mapper Pattern com MapStruct).
    - `dto`: Objetos de transferência (DTO Pattern) separados em request/response.
    - `proxy`: Cliente REST para API externa (Proxy Pattern).

- **Design Patterns:** Repository, DTO, Mapper, Service Layer, Observer (WebSocket).

- **Tecnologias Backend:**
  - **Quarkus:** Framework Java supersônico e subatômico, escolhido pela performance e baixa latência.
  - **Hibernate Panache:** Simplifica a camada de persistência.
  - **Flyway:** Versionamento e migração do banco de dados.
  - **MinIO:** Armazenamento de objetos compatível com S3 (para capas de álbuns).
  - **SmallRye JWT:** Segurança stateless robusta.

- **Tecnologias Frontend:**
  - **Angular 19:** Framework SPA com componentes standalone.
  - **Tailwind CSS 3:** Estilização utility-first.

### 📊 Estrutura de Dados
O diagrama de classes e relacionamentos (incluindo N:N entre Artista e Álbum) pode ser visualizado aqui:
👉 [Diagrama de Classes (DrawDB)](https://www.drawdb.app/editor?shareId=bcdc5c3e7f08ec1491ba96d1a53b06c5)

---

## ✨ Funcionalidades Específicas

### 🔐 Autenticação (JWT)
1.  **Login:** `POST /v1/auth/login` (Gera Access Token de 5min e Refresh Token de 30min).
2.  **Refresh:** `POST /v1/auth/refresh` (Renova o acesso).
3.  **Uso:** Envie o header `Authorization: Bearer <token>`.

### 🔔 WebSocket (Notificações)
Notifica clientes conectados quando um novo álbum é cadastrado.
- **Endpoint:** `ws://localhost:8090/ws/albums`
- **Teste rápido (Console do Navegador):**
  Abra o console (F12) e cole o código abaixo para monitorar:
  ```javascript
  var ws = new WebSocket('ws://localhost:8090/ws/albums');
  ws.onopen = () => console.log('✅ Conectado ao WebSocket!');
  ws.onmessage = (e) => console.log('📩 Recebido:', JSON.parse(e.data));
  ws.onerror = (e) => console.log('❌ Erro:', e);
  ws.onclose = () => console.log('🔌 Desconectado');
  ```

### 🔄 Sincronização de Regionais
Importa e sincroniza dados de uma API externa.
- **Automática:** Diariamente às 06:00.
- **Manual:** `POST /v1/regionais/sync` (Requer permissão ADMIN).
- **Lógica:** Insere novos registros, inativa os ausentes e atualiza os modificados (versionamento).

### 🛡️ Rate Limit
Limita clientes a **10 requisições por minuto** para proteger a API contra abusos.

### 🗄️ Carga Inicial
O banco é populado automaticamente via Flyway com dados de exemplo para demonstrar o relacionamento N:N entre Artistas e Álbuns.
