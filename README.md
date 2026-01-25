## 🧰 Requisitos

- Java 21
- Docker
- Docker Compose
- Maven 3.9+

---

## 🚀 Subindo a aplicação (API + PostgreSQL + MinIO)

Na raiz do projeto, execute:

```bash
./mvnw clean package
docker compose up --build
```
🌐 Acessos

    API: http://localhost:8080
    
    MinIO Console: http://localhost:9001
    
    PostgreSQL: localhost:5432

🔐 Credenciais de acesso

    PostgreSQL
    
        Host: localhost
        
        Porta: 5432
        
        Database: musicdb
        
        Usuário: appuser
        
        Senha: app123

    MinIO
    
        Console: http://localhost:9001
        
        Endpoint API: http://localhost:9000
        
        Credenciais:
        
        Usuário: minioadmin
        
        Senha: minioadmin

🔐 Autenticação (JWT)

    A API utiliza autenticação JWT stateless.
    
    Access Token
    
    Expiração: 5 minutos
    
    Utilizado para acessar endpoints protegidos
    
    Refresh Token
    
    Expiração: 30 minutos
    
    Utilizado para renovar o access token sem novo login
    
        Login
        POST /api/v1/auth/login


    Gera um access token e um refresh token.
    
        Refresh de token
        POST /api/v1/auth/refresh
    
    
    Gera um novo access token a partir de um refresh token válido.
    
    Uso do token
    
    Enviar o access token no header:
    
        Authorization: Bearer <access-token>
    
    Segurança (CORS)
    
    A API aceita requisições apenas da origem configurada:
    
        quarkus.http.cors=true
        quarkus.http.cors.origins=http://localhost:8080
    
    
    Implementação simplificada, adequada para avaliação técnica.