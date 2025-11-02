#!/bin/bash

# 🚀 Script de Teste Rápido - User Profile Service
# Uso: ./TESTE_RAPIDO.sh

echo "🧪 Iniciando Teste do User-Profile-Service"
echo "=========================================="
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Verificar se Docker está rodando
echo "1️⃣ Verificando Docker..."
if ! docker ps > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker não está rodando!${NC}"
    echo "   Abra o Docker Desktop e tente novamente."
    exit 1
fi
echo -e "${GREEN}✅ Docker está rodando${NC}"
echo ""

# 2. Subir PostgreSQL
echo "2️⃣ Subindo PostgreSQL..."
docker-compose up -d
sleep 5
echo -e "${GREEN}✅ PostgreSQL iniciado${NC}"
echo ""

# 3. Verificar se aplicação está rodando
echo "3️⃣ Verificando se aplicação está rodando..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Aplicação já está rodando!${NC}"
    APP_RUNNING=true
else
    echo -e "${YELLOW}⚠️  Aplicação não está rodando${NC}"
    echo "   Execute em outro terminal: ./mvnw spring-boot:run"
    APP_RUNNING=false
fi
echo ""

if [ "$APP_RUNNING" = true ]; then
    # 4. Criar usuário
    echo "4️⃣ Criando usuário de teste..."
    RESPONSE=$(curl -s -X POST http://localhost:8080/api/users \
        -H "Content-Type: application/json" \
        -d '{
            "email": "teste@example.com",
            "password": "SenhaSegura123!",
            "fullName": "Usuário Teste"
        }')
    
    if echo "$RESPONSE" | grep -q "email"; then
        echo -e "${GREEN}✅ Usuário criado com sucesso!${NC}"
        echo "   Resposta: $RESPONSE"
    else
        echo -e "${YELLOW}⚠️  Usuário pode já existir ou erro na criação${NC}"
        echo "   Resposta: $RESPONSE"
    fi
    echo ""
    
    # 5. Fazer login
    echo "5️⃣ Fazendo login..."
    LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{
            "email": "teste@example.com",
            "password": "SenhaSegura123!"
        }')
    
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -n "$TOKEN" ]; then
        echo -e "${GREEN}✅ Login realizado com sucesso!${NC}"
        echo "   Token obtido: ${TOKEN:0:50}..."
        echo ""
        
        # 6. Listar usuários
        echo "6️⃣ Listando usuários (com token)..."
        USERS=$(curl -s -X GET http://localhost:8080/api/users \
            -H "Authorization: Bearer $TOKEN")
        
        if echo "$USERS" | grep -q "email"; then
            echo -e "${GREEN}✅ Usuários listados com sucesso!${NC}"
            echo "   Resposta: $USERS"
        else
            echo -e "${RED}❌ Erro ao listar usuários${NC}"
            echo "   Resposta: $USERS"
        fi
    else
        echo -e "${RED}❌ Erro ao fazer login${NC}"
        echo "   Resposta: $LOGIN_RESPONSE"
    fi
    echo ""
fi

# 7. Executar testes
echo "7️⃣ Executando testes automatizados..."
if [ -f "./mvnw" ]; then
    ./mvnw test -q
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Todos os testes passaram!${NC}"
    else
        echo -e "${RED}❌ Alguns testes falharam${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Maven Wrapper não encontrado${NC}"
    echo "   Execute: mvn test"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}✅ Teste concluído!${NC}"
echo ""
echo "📋 Próximos passos:"
echo "   1. Teste manualmente os endpoints"
echo "   2. Veja o relatório de cobertura: ./mvnw test jacoco:report"
echo "   3. Acesse: http://localhost:8080/actuator/health"
echo ""
echo "🛑 Para parar: docker-compose down"

