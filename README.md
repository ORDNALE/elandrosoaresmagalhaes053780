# API de Gerenciamento Musical

API REST para gerenciamento de artistas e álbuns, desenvolvida como parte de um processo seletivo.

---

## 🧰 Requisitos

- Java 21
- Docker
- Docker Compose
- Maven 3.9+

---

## 🚀 Como Executar

Na raiz do projeto, execute os seguintes comandos:

1.  **Empacotar a aplicação com Maven:**
    ```bash
    ./mvnw clean package
    ```

2.  **Subir os contêineres (API + PostgreSQL + MinIO):**
    ```bash
    docker compose up --build -d
    ```

---

## 🌐 Acessos e Portas

Após a execução, os seguintes serviços estarão disponíveis:

- **API (Quarkus):**
  - **URL Base:** `http://localhost:8090`
  - **Swagger UI (Documentação):** `http://localhost:8090/swagger-ui`

- **PostgreSQL (Banco de Dados):**
  - **Host:** `localhost`
  - **Porta:** `5444`
  - **Database:** `musicdb`
  - **Usuário:** `appuser`
  - **Senha:** `app123`

- **MinIO (Armazenamento de Objetos):**
  - **Endpoint API:** `http://localhost:19000`
  - **Console Web:** `http://localhost:19001`
  - **Usuário:** `minioadmin`
  - **Senha:** `minioadmin`

---

## 🔐 Autenticação (JWT)

A API utiliza autenticação JWT stateless. Para acessar os endpoints protegidos, primeiro obtenha os tokens.

- **Endpoint de Login:** `POST /v1/auth/login`
  - Gera um `accessToken` (expira em 5 minutos) e um `refreshToken` (expira em 30 minutos).

- **Endpoint de Renovação:** `POST /v1/auth/refresh`
  - Gera um novo `accessToken` a partir de um `refreshToken` válido.

- **Como usar:** Envie o `accessToken` no cabeçalho `Authorization` de suas requisições:
  ```
  Authorization: Bearer <seu-access-token>
  ```

---

## 🔔 WebSocket (Notificações)

A API notifica em tempo real quando um novo álbum é cadastrado.

- **Endpoint:** `ws://localhost:8090/ws/albums`

**Como testar:**

1. Acesse o Swagger: `http://localhost:8090/swagger-ui`
2. Abra o Console do navegador (`F12` → Console)
3. Cole e execute:
   ```javascript
   var ws = new WebSocket('ws://localhost:8090/ws/albums');
   ws.onopen = () => console.log('Conectado!');
   ws.onmessage = (e) => console.log('Novo álbum:', JSON.parse(e.data));
   ```
4. Crie um álbum pelo Swagger
5. A notificação aparecerá no Console
