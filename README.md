# API de Gerenciamento Musical

API REST desenvolvida com **Quarkus** para gerenciamento de artistas e álbuns, implementando Clean Architecture e boas práticas de desenvolvimento.

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
- Java 21
- Docker Engine 19.03+ (Compatível com Compose 3.9)
- Docker Compose V2
- Maven 3.9+

### Rodar a Aplicação
```bash
./mvnw clean package
docker compose up --build
```

### Rodar os Testes
```bash
./mvnw test
```

---

## 🌐 Acessos e Credenciais

| Serviço | URL / Host | Porta | Usuário | Senha |
|---------|------------|-------|---------|-------|
| **API** | `http://localhost:8090` | 8090 | - | - |
| **Swagger UI** | [`/swagger-ui`](http://localhost:8090/swagger-ui) | 8090 | - | - |
| **Liveness Probe** | [`/q/health/live`](http://localhost:8090/q/health/live) | 8090 | - | - |
| **Readiness Probe** | [`/q/health/ready`](http://localhost:8090/q/health/ready) | 8090 | - | - |
| **PostgreSQL** | `localhost` | 5444 | `appuser` | `app123` |
| **MinIO API** | `http://localhost:19000` | 19000 | `minioadmin` | `minioadmin` |
| **MinIO Console** | `http://localhost:19001` | 19001 | `minioadmin` | `minioadmin` |

---

## 🏗️ Arquitetura e Decisões Técnicas

O projeto segue os princípios da **Clean Architecture**, visando desacoplamento e testabilidade.

- **Camadas:**
  - `core`: Configurações globais, segurança (JWT), filtros e utilitários.
  - `modules`: Divide o domínio em funcionalidades (Artistas, Álbuns, Regionais).
    - `controllers`: Camada de entrada (REST).
    - `services`: Regras de negócio.
    - `repositories`: Acesso a dados (Pattern Repository com Panache).
    - `entities`: Modelo de dados.
    - `mappers`: Conversão entre DTOs e Entidades (MapStruct).

- **Tecnologias:**
  - **Quarkus:** Framework Java supersônico e subatômico, escolhido pela performance e baixa latência.
  - **Hibernate Panache:** Simplifica a camada de persistência.
  - **Flyway:** Versionamento e migração do banco de dados.
  - **MinIO:** Armazenamento de objetos compatível com S3 (para capas de álbuns).
  - **SmallRye JWT:** Segurança stateless robusta.

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
