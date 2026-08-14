# 🗺️ Plataforma de Turismo

O **Plataforma de Turismo** é uma aplicação Fullstack voltada para a gestão e localização de pontos turísticos. O sistema disponibiliza suporte geoespacial através de mapa via Leaflet, autenticação segura via JWT e login social via Google OAuth2, além de controle rigoroso de permissões por perfil (Tourist, Tour Guide, Spot Manager e Admin).

---

## Tecnologias utilizadas

### **Backend**
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.4+ / 4.x (Spring WebMVC, Spring Security, Spring Data JPA, Spring Cache, Spring Validation)
- **Autenticação e Segurança:** 
  - JWT (Auth0 `java-jwt`)
  - OAuth2 Client (`spring-boot-starter-oauth2-client` - Google Login)
  - Cookies (HttpOnly)
- **Tratamento de erros e Observabilidade**
  - Classe GlobalHanlderException para capturar erros e devolver uma resposta adequada com status code, descrição e horário
  - Classes exception customizadas para retornar um código de status e mensagem de acordo com a natureza da exceção
  - Logs para observabilidade do sistema
- **Banco de Dados e Geoespacial:** 
  - PostgreSQL com extensão **PostGIS** (Hibernate Spatial & JTS, juntamente com serialização customizada para dados geoespaciais seguindo o padrão RFC 7946)
  - H2 Database (suporte para testes/desenvolvimento)
  - Implementação de index e index geoespacial (GiST)
- **Migração de Banco de Dados:** Flyway
- **Cache:** Redis
- **Documentação de API:** Swagger UI
- **Produtividade e Mapeamento:** Lombok, MapStruct
- **Testes:** Testes unitários no backend com JUnit, MockMvc e Mockito

### **Frontend**
- **Framework:** Angular 19
- **Linguagem:** TypeScript
- **Estilização e UI:** Tailwind CSS 4 + DaisyUI
- **Mapas Interativos:** Leaflet + `@bluehalo/ngx-leaflet`
- **Gerenciamento de Estado e Reatividade:** RxJS

---

## Estrutura do repositório

```text
turismo/
├── backend/            # Aplicação Spring Boot (Java 21, REST API, JPA, Security, Swagger)
│   ├── src/main/java   # Código-fonte Java (Controllers, Services, Repositories, Security)
│   └── src/main/resources
│       ├── application.yaml  # Configurações do Spring Boot
│       └── db/migration      # Scripts de migração Flyway (SQL)
│
├── frontend/           # Aplicação Angular 19 (UI, Leaflet Map, DaisyUI, Tailwind CSS)
│   └── src/app/        # Componentes, Serviços, Interceptors e Módulos do Angular
│
└── README.md           # Documentação geral do projeto
```

---

## Controle de acesso, roles e proteção contra IDOR

O sistema implementa uma camada robusta de controle de acesso baseada em perfis (**RBAC**) combinada com validação de propriedade de recursos (**ABAC**) na camada de serviço para mitigar vulnerabilidades de **IDOR (Insecure Direct Object Reference)**.

### **Perfis de usuário (Roles)**

- **`ADMIN`**:
  - Acesso irrestrito a todas as funcionalidades do sistema.
  - Exclusividade na gestão e alteração de entidades globais como Cidades, Estados e Tags.
  - Capacidade de remover qualquer recurso do sistema em caso de necessidade de moderação.

- **`SPOTMANAGER` (Gestor de Ponto Turístico)**:
  - Permissão para cadastrar e administrar pontos turísticos, atividades vinculadas, galeria de fotos, redes sociais e avisos de segurança (`Warns`).
  - **Restrição de Propriedade:** Só pode alterar ou deletar os pontos turísticos e recursos dos quais é o criador/proprietário direto.

- **`TOURGUIDE` (Guia de Turismo)**:
  - Permissão para cadastrar e manter seu perfil profissional de guia de turismo, contatos e áreas de atuação.

- **`TOURIST` (Turista / Usuário Padrão)**:
  - Perfil atribuído por padrão aos novos cadastros e usuários autenticados via Google OAuth2.
  - Acesso à navegação no mapa interativo, busca e filtragem de pontos turísticos, consulta de detalhes e atualização do seu próprio perfil.

---

### **Mitigação de IDOR**

Para prevenir falhas de referência direta a objetos (IDOR), onde um usuário autenticado tenta alterar o ID de um recurso para manipular dados de terceiros, a aplicação aplica checagens rigorosas no Backend:

1. **Anotações de Rota:** As rotas dos Controllers são protegidas com `@PreAuthorize("hasRole('SPOTMANAGER')")` ou `@PreAuthorize("hasAnyRole(...)")`.
2. **Checagem de Propriedade na Camada de Serviço:** Antes de executar qualquer alteração (`PUT`) ou exclusão (`DELETE`), a camada de negócio extrai o ID do usuário autenticado no token JWT (`userId`) e o compara com o proprietário do recurso persistido no banco de dados.

> **Exemplo Prático (Spot Manager):**
> Se o *Gestor A* enviar uma requisição `PUT /api/v1/tourist-spots/{id}` passando o UUID de um ponto turístico que pertence ao *Gestor B*, o serviço de negócio executa a verificação:
> ```java
> if (!touristSpot.getSpotManager().getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
>     throw new UserIsNotOwnerException("Você não tem autorização para isso");
> }
> ```
> O sistema identifica a inconsistência, interrompe a operação e lança uma exceção customizada a depender da validação, retornando um erro HTTP **403 Forbidden**.

---

## Variáveis de ambiente e configurações

### **1. Backend**
As configurações do backend estão localizadas no arquivo [`backend/src/main/resources/application.yaml`](file:///home/yves/projetos_web/turismo/backend/src/main/resources/application.yaml). As seguintes variáveis de ambiente podem ser definidas no sistema operacional ou em um arquivo `.env` para produção/desenvolvimento:

#### **Modelo de arquivo `.env` (Backend):**
```env
# Configurações do Banco de Dados PostgreSQL / PostGIS
POSTGRES_DB=turismo_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua_senha_segura

# Configurações do Redis (Cache)
REDIS_HOST=localhost
REDIS_PORT=6379

# Segurança & Autenticação JWT
JWT_SECRET=superChaveSecretaJWTDoSistemaTurismo!

# Autenticação Social Google OAuth2
GOOGLE_CLIENT_ID=seu-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=seu-google-client-secret
```

---

### **2. Frontend**
As variáveis do frontend são configuradas na pasta environments onde há um arquivo environment.ts. Aqui está onde ela deve ser implementada `frontend/src/app/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',       // URL base da API Spring Boot
  mediaUrl: 'http://localhost:8080/'      // URL base para upload de arquivos/imagens
};
```

---

## Documentação da API (Swagger / OpenAPI)

A API backend conta com a documentação interativa gerada pelo **Swagger UI (Springdoc OpenAPI)**. Através do Swagger, é possível visualizar todas as rotas disponíveis, esquemas de dados, realizar testes de requisições e autenticação com suporte a tokens Bearer JWT.

- **Interface Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) ou [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Especificação OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Nota:** Para testar rotas protegidas no Swagger UI, clique no botão **Authorize** no canto superior direito e insira o seu token JWT no formato `Bearer <seu_token>`.

---

## Testes unitários

O backend utiliza **JUnit 5**, **Mockito**, **MockMvc** e **REST Assured** para testes unitários e de integração de controllers e serviços.

- **Executar todos os testes:**
  ```bash
  cd backend
  ./mvnw test
  ```

- **Executar uma classe de teste específica:**
  ```bash
  cd backend
  ./mvnw test -Dtest=ActivityControllerTest
  ```

- **O que é testado:**
  - Controllers e Endpoints REST (validação de payloads DTO, tratamento de erros HTTP e permissões de segurança).
  - Regras de negócio nas Camadas de Serviço (validações de horários, regras de propriedade IDOR e exceções customizadas).
  - Integrações com Banco de Dados H2 em memória e manipulação de Mappers.


## Como rodar o projeto localmente

### **Pré-requisitos**
- **Java JDK 21** ou superior
- **Node.js** (v18 ou superior) e **npm**
- **PostgreSQL** (com extensão **PostGIS** habilitada)
- **Redis** rodando na porta `6379`

---

### **1. Executando o backend**

1. Navegue até a pasta `backend`:
   ```bash
   cd backend
   ```
2. Configure as variáveis de ambiente necessárias ou ajuste o arquivo `application.yaml`.
3. Certifique-se de que o banco de dados PostgreSQL e o Redis estejam ativos.
4. Execute o projeto via Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *O servidor iniciará por padrão em `http://localhost:8080`.*

---

### **2. Executando o frontend**

1. Navegue até a pasta `frontend`:
   ```bash
   cd frontend
   ```
2. Instale as dependências:
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento:
   ```bash
   npm start
   ```
   *O frontend estará acessível em `http://localhost:4200`.*
