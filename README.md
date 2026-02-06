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
| **CPF** | `053.***.***-61` |
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

```bash
# Backend (JUnit 5 + Mockito + Testcontainers)
cd backend && ./mvnw test

# Frontend (Jasmine + Karma)
cd frontend && npm test
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

> **Por que Arquitetura em Camadas?** Optei por separar `controllers`, `services` e `repositories` porque facilita a manutenção, permite testar cada camada isoladamente e deixa claro onde cada responsabilidade mora. Em projetos maiores, isso evita que regras de negócio vazem para controllers ou que acesso a dados fique espalhado pelo código.

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
- **State Management:** Combinação de **BehaviorSubjects** (estado global reativo) e **Signals** (reatividade granular em componentes).
- **Interceptors & Guards:** Gestão centralizada de Tokens JWT e proteção de rotas.

#### 📂 Estrutura de Pastas
```
frontend/src/app/
├── core/                    # Núcleo da aplicação (singleton services)
│   ├── facades/             # Orquestração: API + State + Notificações
│   ├── guards/              # Proteção de rotas (auth, admin)
│   ├── interceptors/        # HTTP interceptors (JWT, errors)
│   ├── models/              # Interfaces e tipos TypeScript
│   ├── services/            # API services e WebSocket
│   └── state/               # BehaviorSubjects para estado global
├── features/                # Módulos de funcionalidades (lazy loaded)
│   ├── auth/                # Login e autenticação
│   ├── dashboard/           # Tela inicial com estatísticas
│   ├── artists/             # CRUD de artistas
│   └── albums/              # CRUD de álbuns
├── shared/                  # Componentes reutilizáveis
│   └── components/          # Header, Cards, Pagination, Toast, Dialog
└── environments/            # Configurações por ambiente
```

#### 🔄 Padrão Facade + BehaviorSubject
```
Component ──▶ Facade ──▶ API Service
                 │
                 ▼
            StateService (BehaviorSubject)
```

### 🧩 Modelagem de Dados

| Tabela | Descrição |
|--------|-----------|
| `artista` | Bandas ou artistas solo |
| `album` | Obras musicais com título e ano |
| `capa_album` | Metadados das imagens (hash, bucket MinIO) |
| `artista_album` | **N:N** - Permite colaborações entre artistas |
| `genero` | Categorias musicais (Rock, Pop, MPB, etc.) |
| `album_genero` | **N:N** - Álbum pode ter múltiplos gêneros |
| `usuario_artista_favorito` | Favoritos de artistas por usuário |
| `usuario_album_favorito` | Favoritos de álbuns por usuário |
| `regional` | Sincronização com API externa |

> **Por que essa modelagem?** Escolhi relacionamentos N:N (artista-álbum, álbum-gênero) porque refletem a realidade do domínio musical — um álbum pode ter vários artistas (colaborações) e pertencer a múltiplos gêneros. As tabelas de favoritos ficam separadas das entidades core para não poluir o modelo principal e permitir escalar funcionalidades de personalização (recomendações, playlists) sem alterar a estrutura base. Os índices foram criados nas colunas mais filtradas (`tipo`, `ativo`, FKs) para garantir performance em consultas frequentes.

**Visualize o diagrama completo:** [Abrir no DrawDB](https://www.drawdb.app/editor?shareId=bcdc5c3e7f08ec1491ba96d1a53b06c5)

---

### ✨ Funcionalidades da API

| Recurso | Métodos | Descrição |
|---------|---------|-----------|
| `/v1/auth/login` | POST | Login, retorna Access + Refresh Token |
| `/v1/auth/refresh` | POST | Renova Access Token |
| `/v1/artistas` | GET, POST, PUT, DELETE | CRUD de artistas com filtros e paginação |
| `/v1/albuns` | GET, POST, PUT, DELETE | CRUD de álbuns com filtros e paginação |
| `/v1/albuns/{id}/capas` | GET, POST, DELETE | Upload e listagem de capas (MinIO) |
| `/v1/regionais` | GET | Listagem de regionais |
| `/v1/regionais/sync` | POST | Sincronização manual com API externa |

**Paginação:** `?page=0&size=10&sort=asc` | **Filtros:** `?nome=`, `?tipo=SOLO|BANDA`

**WebSocket:** `ws://localhost:8090/ws/albums` — notifica novos álbuns em tempo real.

**Documentação completa:** [Swagger UI](http://localhost:8090/swagger-ui)

## ✅ Requisitos Atendidos

### Backend - Requisitos Gerais
- [x] **a) Segurança (CORS):** Acesso restrito a origens confiáveis.
- [x] **b) Autenticação JWT:** Access Token (5 min) + Refresh Token (30 min).
- [x] **c) POST, PUT, GET:** Implementados para Artistas, Álbuns e Capas (DELETE incluso).
- [x] **d) Paginação:** Consultas paginadas com `page` e `size`.
- [x] **e) Consultas parametrizadas:** Filtro por tipo (`SOLO`/`BANDA`).
- [x] **f) Consulta por nome com ordenação:** Busca parcial + ordenação `asc`/`desc`.
- [x] **g) Upload de capas:** Múltiplas imagens via multipart.
- [x] **h) Armazenamento MinIO:** Imagens no MinIO, metadados no banco.
- [x] **i) Links pré-assinados:** Presigned URLs com expiração de 30 min.
- [x] **j) Versionamento:** Endpoints sob `/v1/`.
- [x] **k) Flyway Migrations:** Criação de tabelas e carga inicial.
- [x] **l) OpenAPI/Swagger:** Documentação em `/swagger-ui`.

### Backend - Requisitos Sênior
- [x] **a) Health Checks:** Liveness e Readiness via SmallRye Health.
- [x] **b) Testes unitários:** JUnit 5 + Mockito + Testcontainers.
- [x] **c) WebSocket:** Notificação em tempo real a cada novo álbum.
- [x] **d) Rate Limit:** 10 req/min por cliente (Bucket4j).
- [x] **e) Regionais:** Sincronização com API externa conforme regras do edital.

### Frontend - Requisitos Gerais
- [x] **a) Tela Inicial - Listagem de Artistas:**
  - [x] Consulta e exibição em cards responsivos (nome + nº de álbuns)
  - [x] Campo de busca por nome
  - [x] Ordenação asc/desc
  - [x] Paginação
- [x] **b) Tela de Detalhamento do Artista:**
  - [x] Exibição de álbuns associados ao clicar no artista
  - [x] Mensagem quando não há álbuns
  - [x] Exibição de capas dos álbuns
- [x] **c) Tela de Cadastro/Edição:**
  - [x] Formulário para inserir/editar artistas
  - [x] Formulário para adicionar/editar álbuns
  - [x] Upload de capas via MinIO
- [x] **d) Autenticação:**
  - [x] Acesso ao front exige login
  - [x] Autenticação JWT consumindo endpoint da API
  - [x] Gerenciamento de expiração e renovação (silent refresh)
- [x] **e) Arquitetura:**
  - [x] Boas práticas (modularização, componentização, services)
  - [x] Layout responsivo
  - [x] Tailwind CSS
  - [x] Lazy Loading Routes
  - [x] Paginação
  - [x] TypeScript

### Frontend - Requisitos Sênior
- [x] **b) Testes unitários:** 8 arquivos de teste (Facades, Guards, Interceptors, Services).
- [x] **c) WebSocket:** Exibição de notificação toast a cada novo álbum cadastrado.
- [x] **e) Padrão Facade + BehaviorSubject:** Implementado conforme documentado na arquitetura.

### Instruções Gerais
- [x] Docker-compose com BD, MinIO, API e Frontend.
- [x] README.md com documentação, dados de inscrição e instruções.
- [x] Relacionamento N:N entre Artista e Álbum.
- [x] Carga inicial com exemplos do edital (Serj Tankian, Mike Shinoda, Michel Teló, Guns N' Roses).

