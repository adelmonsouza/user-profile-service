# 📑 Business Plan: User-Profile-Service

## 1. Visão de Negócio

### Problema a Resolver

A empresa precisa de uma **fonte de verdade única**, segura e centralizada para gerenciar a identidade de **milhões de usuários**, garantindo consistência e rápida recuperação de dados em diferentes sistemas (Web, Mobile, BI).

### Proposta de Valor

Entregar uma API de **alta performance** e **disponibilidade** para operações básicas de usuário (CRUD) e autenticação (AuthN/AuthZ), separando a lógica de identidade do restante dos microsserviços.

**Benefícios chave:**
- **Isolamento de falhas:** Problemas em um serviço não afetam outros
- **Escalabilidade independente:** Escale apenas o que precisa
- **Desenvolvimento paralelo:** Times diferentes podem trabalhar simultaneamente
- **Tecnologia independente:** Cada serviço pode usar a stack ideal

## 2. Requisitos Funcionais

### Core (MVP)

- ✅ Gerenciar ciclo de vida do usuário (CRUD completo)
- ✅ Prover endpoint de login seguro (Gerar JWT)
- ✅ Validar dados de entrada (Email, Senha) seguindo Bean Validation
- ✅ Buscar usuários por ID ou Email

### Próximas Iterações

- 🔄 Recuperação de senha (Forgot Password)
- 🔄 Verificação de email (Email Confirmation)
- 🔄 Perfil com foto (Upload de imagem)
- 🔄 Auditoria de ações (Audit Logs)

## 3. Requisitos Não-Funcionais

### Performance

- **Latência:** Tempo de resposta < 100ms para operações de leitura
- **Throughput:** Suportar 1000 req/s por instância
- **Escalabilidade:** Escala horizontal sem downtime

### Segurança

- **Autenticação:** JWT com expiração configurável
- **Senhas:** Hashing com BCrypt (work factor 12)
- **HTTPS:** Obrigatório em produção
- **Rate Limiting:** Proteção contra brute force

### Disponibilidade

- **SLA:** 99.9% uptime (aproximadamente 8h downtime/ano)
- **Backup:** Snapshots diários do banco
- **Monitoramento:** Health checks a cada 30s

### Qualidade

- **Testes:** Cobertura mínima de 80%
- **Code Review:** Obrigatório antes de merge
- **CI/CD:** Deploy automatizado via GitHub Actions

## 4. Estratégia Técnica (O 'Como' e 'Por Quê')

### Arquitetura: Microsserviço de Domínio

**Vantagem:** Isolamento de falhas e escalabilidade horizontal independente.

**Como funcionará:**
```
┌─────────────────┐
│  API Gateway    │
└────────┬────────┘
         │
    ┌────▼─────────────────────────────────┐
    │  User-Profile-Service (este)         │
    │  ┌──────────┐  ┌──────────┐         │
    │  │ Controller│─▶│ Service  │         │
    │  └──────────┘  └─────┬────┘         │
    │                      │                │
    │  ┌───────────────────▼────┐          │
    │  │    Repository          │          │
    │  └────────────────────┬───┘          │
    └───────────────────────┼──────────────┘
                            │
                    ┌───────▼───────┐
                    │  PostgreSQL   │
                    └───────────────┘
```

### Decisões Técnicas Chave

#### 1. Spring Data JPA + Padrão Repository

**Justificativa:** Abstração do acesso a dados, facilitando:
- Troca do banco de dados sem impacto no código
- Testes unitários com mocks
- Redução de boilerplate
- Suporte nativo a queries complexas

**Alternativa considerada:** MyBatis
**Por que não:** Mais verboso, menos integrado com Spring

#### 2. DTOs (Data Transfer Objects) vs. Expor Entidades Diretamente

**Justificativa:**
- **Segurança:** Evita expor campos sensíveis (senha, tokens)
- **Desacoplamento:** API independente do modelo de dados
- **Flexibilidade:** Pode mudar entidade sem quebrar contratos
- **Performance:** Controlar exatamente quais dados transferir

**Custo:** Mais código para mapear
**Benefício:** Muito maior para manutenção e segurança

#### 3. JWT (JSON Web Tokens) para Autenticação

**Justificativa:**
- **Stateless:** Não precisa de sessão no servidor
- **Escalável:** Funciona em qualquer instância
- **Mobile-friendly:** Ideal para apps nativos

**Alternativa considerada:** OAuth 2.0 com Authorization Server
**Por que não agora:** Complexidade desnecessária para MVP

#### 4. Testcontainers para Testes de Integração

**Justificativa:**
- Testa contra banco real (PostgreSQL)
- Catch bugs que não aparecem em mocks
- CI/CD friendly (dockerized)

**Custo:** Testes mais lentos
**Benefício:** Confiança muito maior

### Tecnologias Selecionadas

| Camada | Tecnologia | Por que? |
|--------|-----------|----------|
| **Framework** | Spring Boot 3.2+ | Padrão de mercado, maduro, eco-sistema |
| **Linguagem** | Java 21 | LTS, Virtual Threads, Records |
| **Banco** | PostgreSQL 15 | Open source, robusto, JSON support |
| **Container** | Docker | Standard, CI/CD ready |
| **CI/CD** | GitHub Actions | Gratuito, integrado |
| **Testes** | JUnit 5 + Mockito | Padrão Java |
| **Segurança** | Spring Security | Enterprise-grade, mantido |

## 5. Modelo de Dados

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT email_format CHECK (email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$')
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(enabled);
```

## 6. Métricas de Sucesso

### KPIs Técnicos

- **Latência:** Login < 100ms (p95)
- **Disponibilidade:** 99.9% uptime
- **Testes:** ≥ 80% cobertura
- **Zero:** Vulnerabilidades críticas

### KPIs de Negócio

- **Onboarding:** Suporte a 10k novos usuários/dia
- **Login:** 100k logins/dia
- **Escalabilidade:** 1 -> 10 instâncias sem code change

## 7. Roadmap de Evolução

### Fase 1 (MVP) - 2 semanas
- CRUD básico
- Login com JWT
- Testes unitários
- CI/CD básico

### Fase 2 - 4 semanas
- Forgot Password
- Email Verification
- Rate Limiting
- Observability (Prometheus/Grafana)

### Fase 3 - 8 semanas
- Upload de foto (S3)
- Audit Logs
- Multi-factor Authentication (MFA)
- Integração com SSO

## 8. Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|-------|--------------|---------|-----------|
| **Banco de dados falha** | Média | Alto | Replicação, backups diários, failover automático |
| **Ataque DDoS** | Baixa | Alto | Rate limiting, CloudFlare |
| **Vazamento de senhas** | Baixa | Crítico | BCrypt, nunca logar senhas, auditoria |
| **Performance degrada** | Média | Médio | Monitoramento, load testing, escalabilidade horizontal |

## 9. Conclusão

Este projeto demonstra **compreensão de arquitetura** e **visão de negócio**, não apenas conhecimento técnico.

**Cada decisão foi pensada considerando:**
- Escalabilidade
- Manutenibilidade
- Segurança
- Custo/benefício

**Isso é o que faz um desenvolvedor sênior.**

---

**Próximos passos:** Implementar, testar, documentar, postar no LinkedIn! 🚀

