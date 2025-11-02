# 🚀 Projeto 1/30: User-Profile-Service

**Conceito:** Microsserviço de Gerenciamento de Usuários (Inspirado em Facebook/X)

## 🎯 Business Plan & Propósito

Este microsserviço simula a API central de gerenciamento de usuários de uma rede social. Seu valor de negócio reside em oferecer uma fonte de dados única, segura e altamente disponível (Single Source of Truth) para todos os demais serviços (Postagens, Mensagens, Recomendações).

**Escalabilidade:** Projetado para ser horizontalmente escalável, separando a lógica de usuários do restante da aplicação para lidar com milhões de requisições.

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.2+
- **Dependências:** Spring Web, Spring Data JPA, Spring Security, Validation
- **Banco de Dados:** PostgreSQL 15
- **Containerização:** Docker
- **Testes:** JUnit 5, Mockito, Testcontainers
- **CI/CD:** GitHub Actions

## 🏗️ Arquitetura e Boas Práticas

### Estrutura em Camadas (Controller -> Service -> Repository)

Aplicação estrita do princípio de "Controllers Magros" (Thin Controllers).

### DTOs (Data Transfer Objects)

Uso de DTOs para evitar expor entidades JPA (Proteção e Desacoplamento).

### Segurança (Spring Security/JWT)

Implementação do fluxo OAuth 2.0 via JWT para autenticação.

### Testes de Unidade e Integração

Cobertura mínima de 80% focada nas regras de negócio (Camada Service).

## 👨‍💻 Como Rodar o Projeto

### Pré-requisitos

- Java 21
- Maven 3.8+
- Docker e Docker Compose

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/adelmonsouza/user-profile-service.git
   cd user-profile-service
   ```

2. **Subir o banco de dados:**
   ```bash
   docker-compose up -d
   ```

3. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Teste os endpoints:**
   ```bash
   # Listar usuários
   curl http://localhost:8080/api/users

   # Criar usuário
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{
       "email": "user@example.com",
       "password": "SecurePass123!",
       "fullName": "João Silva"
     }'
   ```

## 📊 Endpoints da API

### Autenticação
- `POST /api/auth/login` - Login e obtenção de JWT

### Usuários
- `GET /api/users` - Listar todos os usuários
- `GET /api/users/{id}` - Buscar usuário por ID
- `POST /api/users` - Criar novo usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

## 🧪 Executar Testes

```bash
# Todos os testes
./mvnw test

# Com cobertura
./mvnw test jacoco:report
```

## 📈 Métricas de Sucesso

- **Latência:** Tempo de resposta da API de login < 100ms
- **Qualidade:** Cobertura de testes ≥ 80%
- **Disponibilidade:** 99.9% uptime

## 🔗 Links Úteis

- **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html
- **Actuator:** http://localhost:8080/actuator

## 📝 Documentação

Veja `Business_Plan.md` para detalhes sobre decisões de arquitetura e justificativas técnicas.

---

**#30DiasJava | #SpringBoot | #Microsserviços | #CleanCode**

