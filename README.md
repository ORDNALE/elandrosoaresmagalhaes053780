# Sistema de Gerenciamento Musical

Aplicação fullstack para gerenciamento de artistas e álbuns.

```
├── backend/   → API REST com Quarkus (Java 21)
├── frontend/  → Aplicação Angular 19 + Tailwind CSS
└── docker-compose.yml
```

---

## 📌 Visão Geral
Este sistema é uma solução fullstack desenvolvida para o gerenciamento robusto de um catálogo musical. O objetivo é resolver o desafio técnico de orquestrar dados relacionados (N:N), persistência de arquivos (capas), segurança e performance, tudo em um ambiente containerizado pronto para produção.

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
- Docker Engine 19.03+ (ou [Docker Desktop](https://www.docker.com/products/docker-desktop/))
- Docker Compose V2
- Portas **4201**, **8090**, **5444**, **19000** e **19001** livres no host

### Rodar a Aplicação
O projeto está totalmente containerizado. Um único comando inicia todo o ecossistema (Frontend, API, Banco de Dados e MinIO):

```bash
docker compose up -d --build
```

> Na primeira execução, o build pode demorar alguns minutos para baixar as imagens base e compilar o backend/frontend.

Após a conclusão, acesse o frontend em **http://localhost:4201**.

### Parar a Aplicação

```bash
docker compose down
```

Para remover também os volumes (banco de dados e arquivos do MinIO):

```bash
docker compose down -v
```

### 🧪 Como Testar

**Estratégia de Testes:**
Os testes de integração não dependem do `docker-compose` da aplicação principal. A estratégia combina **JUnit 5** e **REST Assured** para validação de endpoints, juntamente com **Mockito** para isolamento de componentes unitários quando necessário.
A infraestrutura de banco de dados e storage é provisionada dinamicamente via **Testcontainers**, garantindo um ambiente estéril e fiel à produção para cada execução.

**Para executar:**
```bash
cd backend && ./mvnw test
```

---

## 🌐 Acessos, Portas e Credenciais

Todas as portas foram configuradas em valores não-padrão para evitar conflitos com serviços já em execução na máquina do avaliador.

| Serviço | URL / Host | Porta | Usuário | Senha |
|---------|------------|-------|---------|-------|
| **Frontend** | `http://localhost:4201` | 4201 | `appuser` | `app123` |
| **API** | `http://localhost:8090` | 8090 | - | - |
| **Swagger UI** | [`/swagger-ui`](http://localhost:8090/swagger-ui) | 8090 | - | - |
| **Liveness Probe** | [`/q/health/live`](http://localhost:8090/q/health/live) | 8090 | - | - |
| **Readiness Probe** | [`/q/health/ready`](http://localhost:8090/q/health/ready) | 8090 | - | - |
| **PostgreSQL** | `localhost` | 5444 | `appuser` | `app123` |
| **MinIO API** | `http://localhost:19000` | 19000 | `minioadmin` | `minioadmin` |
| **MinIO Console** | `http://localhost:19001` | 19001 | `minioadmin` | `minioadmin` |

---

## 🏗️ Arquitetura e Decisões Técnicas
O projeto adota uma abordagem fullstack moderna, com separação clara de responsabilidades tanto no servidor quanto no cliente.

### 🔙 Arquitetura Backend (Java 21 + Quarkus)
O backend foi construído sobre o framework **Quarkus** para garantir baixo consumo de memória e inicialização rápida (Supersonic Subatomic Java).
- **Core & Camadas:** Arquitetura em camadas (`Layered Architecture`) separando `controllers`, `services`, e `repositories`.
- **Persistência:** **Hibernate ORM com Panache** simplifica a camada de dados, enquanto o **PostgreSQL 16** garante integridade relacional.
- **Migração:** Versionamento de banco automatizado com **Flyway**.
- **Armazenamento:** **MinIO** (S3 Compatible) desacopla o armazenamento de arquivos binários (capas) do banco de dados.
- **Segurança:** **SmallRye JWT** implementa autenticação RBAC stateless.
- **Patterns:** Repository, DTO, Mapper (**MapStruct**), Observer (**WebSocket**).
- **Testes:** **JUnit 5**, **Mockito** e **REST Assured** integrados com **Testcontainers** para testes de integração reais.

### 🖥️ Arquitetura Frontend (Angular 19)
O frontend utiliza **Angular 19** com foco em performance e modernidade, estilizado com **Tailwind CSS 3** para responsividade.
- **Standalone Components:** Eliminação de módulos para reduzir boilerplate e facilitar Tree Shaking.
- **Lazy Loading:** Módulos de funcionalidades carregados sob demanda.
- **Facade Pattern:** Abstração da lógica de negócios e comunicação com API, mantendo os componentes limpos.
- **State Management (Signals):** Uso de reatividade granular com Signals no lugar de BehaviorSubjects tradicionais onde aplicável.
- **Interceptors & Guards:** Gestão centralizada de Tokens JWT e proteção de rotas.

### 🧩 Modelagem de Dados
O banco de dados foi desenhado para garantir integridade e performance, utilizando as seguintes estratégias:

- **Entidades Principais:**
  - `Artista`: Representa bandas ou artistas solo.
  - `Album`: Obras musicais lançadas.
  - `CapaAlbum`: Armazena metadados da imagem (hash, bucket, tamanho), desacoplando o binário (MinIO) dos dados relacionais.
- **Relacionamento N:N (Muitos para Muitos):**
  - Implementado entre `Artista` e `Album` através da tabela associativa `artista_album`. Isso permite que um álbum pertença a múltiplos artistas (ex: "Feat" ou colaborações) e um artista tenha múltiplos álbuns.
- **Auditoria e Indices:**
  - Índices criados em colunas de alta seletividade (`tipo`, `ativo`, chaves estrangeiras) para otimizar consultas conforme demonstrado no arquivo de migração.

O diagrama de classes e relacionamentos pode ser visualizado aqui:
👉 [Diagrama de Classes (DrawDB)](https://www.drawdb.app/editor?shareId=bcdc5c3e7f08ec1491ba96d1a53b06c5)

---

### ✨ Funcionalidades da API

### 📌 Versionamento
Todos os endpoints utilizam o prefixo `/v1/`, permitindo evolução futura sem quebra de contrato.

### 🔐 Autenticação (JWT)
- **Login:** `POST /v1/auth/login` — retorna Access Token + Refresh Token.
- **Renovação:** `POST /v1/auth/refresh` — gera novo Access Token a partir do Refresh Token.
- **Expiração do Access Token:** 5 minutos.
- **Expiração do Refresh Token:** 30 minutos.
- **Uso:** Header `Authorization: Bearer <token>`.

### 📄 Endpoints REST (POST, PUT, GET, DELETE)

| Recurso | POST | GET (lista) | GET (detalhe) | PUT | DELETE |
|---------|------|-------------|---------------|-----|--------|
| `/v1/artistas` | Criar artista | Listar com filtros | Por ID | Atualizar | Remover |
| `/v1/albuns` | Criar álbum | Listar com filtros | Por ID | Atualizar | Remover |
| `/v1/albuns/{id}/capas` | Upload de capas | Listar capas | Capa por ID | - | Remover |
| `/v1/regionais` | Sync manual (`/sync`) | Listar com filtros | - | - | - |

### 🔍 Paginação e Filtros

Consultas paginadas com os seguintes parâmetros:

| Parâmetro | Tipo | Default | Descrição |
|-----------|------|---------|-----------|
| `page` | int | 0 | Número da página (base 0) |
| `size` | int | 10 | Itens por página |
| `sort` | string | asc | Ordenação alfabética (`asc` ou `desc`) |
| `nome` | string | - | Busca parcial por nome do artista (case-insensitive) |
| `tipo` | enum | - | Filtra por tipo: `SOLO` (cantores) ou `BANDA` |
| `tituloAlbum` | string | - | Busca parcial por título do álbum |

Formato de resposta paginada:
```json
{
  "page": 0,
  "size": 10,
  "total": 42,
  "pageCount": 5,
  "content": [...]
}
```

### 🖼️ Upload de Capas e Links Pré-assinados
- Upload de uma ou mais imagens por requisição via `multipart/form-data` em `POST /v1/albuns/{id}/capas`.
- Imagens armazenadas no **MinIO** (bucket `capa-albuns`), com apenas metadados persistidos no banco (hash, bucket, content-type, tamanho).
- Recuperação via **links pré-assinados (presigned URLs)** com expiração de **30 minutos**.

### 📖 Documentação OpenAPI/Swagger
Endpoints documentados com anotações OpenAPI. Swagger UI disponível em [`/swagger-ui`](http://localhost:8090/swagger-ui).

### 🔔 Notificações em Tempo Real (WebSocket)
Monitoramento em tempo real de novos álbuns cadastrados.
- **Endpoint:** `ws://localhost:8090/ws/albums`
- **Uso:** Clientes conectados recebem payload JSON a cada novo álbum criado.

### 🔄 Sincronização de Regionais
Importação e versionamento de dados da API externa (`https://integrador-argus-api.geia.vip/v1/regionais`).
- **Automática:** Agendada diariamente às 06:00.
- **Manual:** `POST /v1/regionais/sync` (Admin).

Regras de sincronização:

| Cenário | Ação |
|---------|------|
| Regional presente na API externa mas ausente na base interna | **Inserir** novo registro com `ativo=true` |
| Regional presente na base interna mas ausente na API externa | **Inativar** (`ativo=false`) |
| Regional presente em ambos mas com nome alterado | **Inativar** o registro antigo e **inserir** novo com `ativo=true` |

### 🛡️ Proteção da API
- **Rate Limit:** 10 requisições por minuto por cliente, implementado com **Bucket4j**. Identificação por token JWT ou IP. Retorna HTTP 429 com headers `X-Rate-Limit-*`.
- **CORS:** Restrito a origens confiáveis (`localhost:4201`, `localhost:4200`, `localhost`). Métodos permitidos: GET, POST, PUT, DELETE, PATCH, OPTIONS.

## ✅ Requisitos Atendidos

### Requisitos Gerais
- [x] **a) Segurança (CORS):** Acesso restrito a origens confiáveis (`localhost:4201`, `localhost:4200`, `localhost`).
- [x] **b) Autenticação JWT:** Access Token com expiração de 5 minutos e Refresh Token com expiração de 30 minutos.
- [x] **c) POST, PUT, GET:** Implementados para Artistas, Álbuns e Capas de Álbum (DELETE também incluso).
- [x] **d) Paginação:** Consulta de álbuns paginada com parâmetros `page` e `size`.
- [x] **e) Consultas parametrizadas:** Filtro por tipo de artista (`SOLO` para cantores, `BANDA` para bandas), com suporte a múltiplos tipos simultâneos.
- [x] **f) Consulta por nome com ordenação:** Busca parcial por nome do artista (case-insensitive) com ordenação alfabética (`asc`/`desc`).
- [x] **g) Upload de capas:** Upload de uma ou mais imagens por requisição via multipart.
- [x] **h) Armazenamento no MinIO:** Imagens armazenadas no MinIO (S3), com metadados no banco relacional.
- [x] **i) Links pré-assinados:** Recuperação de capas via presigned URLs com expiração de 30 minutos.
- [x] **j) Versionamento de endpoints:** Todos os endpoints sob o prefixo `/v1/`.
- [x] **k) Flyway Migrations:** Criação de tabelas e carga inicial automatizadas via migration SQL.
- [x] **l) OpenAPI/Swagger:** Documentação interativa em `/swagger-ui` com anotações em todos os endpoints.
- [x] **Relacionamento N:N:** Artista-Álbum via tabela associativa `artista_album`, com suporte a colaborações.

### Requisitos Sênior
- [x] **a) Health Checks:** Liveness (`/q/health/live`) e Readiness (`/q/health/ready`) via SmallRye Health.
- [x] **b) Testes unitários:** JUnit 5 + Mockito para serviços e autenticação; Testcontainers para PostgreSQL e MinIO.
- [x] **c) WebSocket:** Notificação em tempo real a cada novo álbum cadastrado via `ws://localhost:8090/ws/albums`.
- [x] **d) Rate Limit:** 10 requisições por minuto por cliente (Bucket4j), identificação por JWT ou IP.
- [x] **e) Regionais:** Importação da API externa com atributo `ativo`; sincronização com regras: novo insere, ausente inativa, alterado inativa antigo e cria novo.

### Instruções Atendidas
- [x] **Repositório GitHub** com histórico de commits.
- [x] **README.md** com documentação, dados de inscrição, vaga e instruções de execução/teste.
- [x] **Relacionamento N:N** entre Artista e Álbum.
- [x] **Carga inicial** com os exemplos do edital (Serj Tankian, Mike Shinoda, Michel Teló, Guns N' Roses).
- [x] **Docker:** Aplicação empacotada como imagens Docker, orquestrada via `docker-compose` (API + Frontend + PostgreSQL + MinIO).

