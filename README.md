# 🍽️ Tech Challenge - Fase 02 | API de Gestão de Restaurantes

Sistema de gerenciamento de restaurantes, cardápios e usuários desenvolvido com **Java 21**, **Spring Boot 3.4.x** e **MySQL 8.0**, totalmente containerizado com **Docker** e **Docker Compose** e documentado via **OpenAPI (Swagger UI)**.

---

## 🏗️ Arquitetura e Tecnologias

O projeto adota os princípios da **Clean Architecture**, promovendo desacoplamento, testabilidade e facilidade de manutenção.

* **Linguagem:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4.3
* **Persistência & Banco de Dados:** Spring Data JPA / Hibernate, MySQL 8.0
* **Documentação:** Springdoc OpenAPI 2 (Swagger UI)
* **Containerização:** Docker (Multi-stage build) & Docker Compose
* **Build Tool:** Maven

---

## 📁 Estrutura do Repositório

```text
.
├── docs/
│   └── gestao_restaurantes.postman_collection.json  # Collection do Postman para testes
├── src/                                             # Código-fonte da aplicação (Clean Architecture)
├── .env.example                                      # Modelo de variáveis de ambiente
├── .gitignore                                        # Arquivos ignorados pelo Git
├── Dockerfile                                        # Script de build multi-stage da API
├── docker-compose.yml                                # Orquestrador dos containers (API + MySQL)
├── pom.xml                                           # Gerenciador de dependências Maven
└── README.md                                         # Documentação do projeto

```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução.
* [Git](https://git-scm.com/) para clonar o repositório.

### 1. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto copiando a estrutura do `.env.example`:

```bash
cp .env.example .env

```

Conteúdo do `.env`:

```env
DB_NAME=gestao_restaurantes_2
DB_ROOT_PASSWORD=melebibi
DB_USER=root
DB_PASSWORD=melebibi
DB_PORT=3306

```

### 2. Subir os Containers via Docker Compose

Na raiz do projeto, execute o comando para compilar e subir os serviços:

```bash
docker compose up -d --build

```

O Docker Compose inicializará o banco de dados MySQL e aguardará o estado de *healthcheck* antes de subir a API Spring Boot.

---

## 📌 Acessando a API e Documentação

Quando os containers estiverem ativos, a API estará acessível nos seguintes endereços:

* **URL Base da API (Docker):** `http://localhost:8082`
* **Swagger UI (Documentação Interativa):** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
* **OpenAPI Specs (JSON):** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)

> **Nota para execução local sem Docker (via IDE):**
> Caso execute a aplicação diretamente na máquina, a URL base será `http://localhost:8080`.

---

## 🧪 Testando as Requisições via Postman

Disponibilizamos uma collection pronta com todos os endpoints mapeados e exemplificados:

1. Abra o **Postman**.
2. Clique em **Import** (canto superior esquerdo).
3. Selecione o arquivo `docs/gestao_restaurantes.postman_collection.json` localizado no projeto.
4. Certifique-se de que a variável `base_url` no Postman aponta para `http://localhost:8082`.

---

## 🛡️ Endpoints Principais

A API inclui operações CRUD e regras de negócio para os domínios:

* `/usuarios` — Gestão e cadastro de usuários e perfis de acesso.
* `/tipos-usuario` — Categorização de papéis de usuários no sistema.
* `/restaurantes` — Cadastro, edição e consulta de estabelecimentos.
* `/itens-cardapio` — Gerenciamento dos pratos e itens vinculados aos restaurantes.

---

## 🛠️ Utilitários de Gerenciamento

```bash
# Acompanhar os logs da API em tempo real
docker compose logs -f api-app

# Acompanhar os logs do banco MySQL
docker compose logs -f mysql-db

# Parar os containers
docker compose down

# Parar e remover volumes (resetar banco de dados)
docker compose down -v

```