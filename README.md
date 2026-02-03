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

### Rodar a Aplicação
O projeto está totalmente containerizado. Para iniciar todo o ecossistema (Frontend, API, Banco de Dados, MinIO), utilize o Docker Compose:

```bash
docker-compose up -d --build
```
> ⏳ Na primeira execução, o build pode demorar alguns minutos para baixar as imagens e compilar o backend/frontend.


### 🧪 Como Testar

**Estratégia de Testes:**
Os testes de integração não dependem do `docker-compose` da aplicação principal. A estratégia combina **JUnit 5** e **REST Assured** para validação de endpoints, juntamente com **Mockito** para isolamento de componentes unitários quando necessário.
A infraestrutura de banco de dados e storage é provisionada dinamicamente via **Testcontainers**, garantindo um ambiente estéril e fiel à produção para cada execução.

**Para executar:**
```bash
cd backend && ./mvnw test
```

---

## 🌐 Acessos e Credenciais

| Serviço | URL / Host | Porta | Usuário | Senha |
|---------|------------|-------|---------|-------|
| **Frontend** | `http://localhost` | 80 | `appuser` | `app123` |
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

### ✨ Funcionais Principais

### 🔐 Autenticação
- **Login:** `POST /v1/auth/login` (Retorna Access + Refresh Token).
- **Renovação:** `POST /v1/auth/refresh`.
- **Uso:** Header `Authorization: Bearer <token>`.

### 🔔 Notificações (WebSocket)
Monitoramento em tempo real de novos álbuns.
- **Endpoint:** `ws://localhost:8090/ws/albums`
- **Uso:** Clientes conectados recebem payload JSON a cada novo cadastro.

### 🔄 Sincronização de Regionais
Importação e versionamento de dados da API externa.
- **Automática:** Agendada para 06:00.
- **Manual:** `POST /v1/regionais/sync` (Admin).

### 🛡️ Proteção da API
- **Rate Limit:** 10 req/min por cliente.
- **CORS:** Restrito a origens confiáveis.

## ✅ Requisitos Atendidos

### 🧩 Funcionais
- [x] **API RESTful:** CRUD completo de Artistas e Álbuns com paginação, ordenação e filtros dinâmicos.
- [x] **Relacionamento N:N:** Gerenciamento correto entre Artistas e Álbuns, permitindo colaborações e múltiplos vínculos.
- [x] **Frontend – Telas Obrigatórias:**
  - **Inicial:** Listagem em cards responsivos, busca textual e ordenação.
  - **Detalhes:** Visualização completa do artista e seus álbuns relacionados.
  - **Cadastro/Edição:** Formulários reativos com validação e associação N:N.
  - **Autenticação:** Login obrigatório com JWT e renovação automática via Interceptor.
- [x] **Upload de Arquivos:** Armazenamento de capas de álbuns no MinIO, com persistência apenas de metadados no banco relacional.
- [x] **Notificações em Tempo Real:** Comunicação via WebSocket para aviso imediato de novos álbuns cadastrados.
- [x] **Integração Externa:** Sincronização de dados de Regionais via API externa, com versionamento, atualização incremental e inativação lógica.

---

### 🏗️ Não Funcionais (Arquitetura & Qualidade)
- [x] **Arquitetura Backend:** Camadas bem definidas (Controller, Service, Repository, DTO e Mapper).
- [x] **Padrões de Projeto:** Repository, DTO, Mapper, Service Layer, Facade (Frontend) e Observer (WebSocket).
- [x] **Arquitetura Frontend:**
  - Facade Pattern para desacoplamento entre componentes e regras de negócio.
  - Gerenciamento de estado baseado em **Angular Signals**, garantindo reatividade previsível.
  - Lazy Loading por funcionalidade e TypeScript em modo estrito.
- [x] **Segurança:** Autenticação JWT com controle de roles, refresh token e CORS restritivo.
- [x] **Containerização:** Docker Compose orquestrando Frontend, Backend, PostgreSQL e MinIO.
- [x] **Resiliência:** Health Checks (Liveness/Readiness), Rate Limiting e Graceful Shutdown.
- [x] **Persistência:** Banco PostgreSQL com versionamento controlado via Flyway.
- [x] **Testabilidade:** Testes unitários e de integração utilizando JUnit 5, REST Assured e Testcontainers.
