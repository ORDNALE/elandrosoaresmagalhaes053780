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