# IdeiaWare — Plataforma

Plataforma acadêmica composta por dois módulos:

- **IdeiaWare LIC** (`/LIC`) — gestão de ideias: cadastro, colaboração, storytelling, canvas e exportação.
- **Toolkit2025** (`/toolkit`) — ferramentas de design thinking vinculadas às ideias do LIC (Persona, POV, Empathy Map).

Stack: Tomcat 9 · Java 21 · MySQL 8 · Hibernate 5.6 · Spring MVC 5.3

---

## Status de uma Ideia

| Código | Significado |
|--------|-------------|
| PE | Pendente — aguardando confirmação do administrador |
| VA | Validado — aceito, apto a buscar participantes |
| RE | Rejeitado pelo admin |
| DE | Desenvolvimento — em andamento |
| ST | Storytelling |
| CF | Caixa de Ferramentas |
| CV | Canvas |
| FN | Finalizado |

## Status de Grupo

| Código | Significado |
|--------|-------------|
| AB | Aberto para novos usuários |
| FE | Fechado para novos usuários |

---

## Rodar com Docker

### Pré-requisitos
- Docker e Docker Compose instalados
- Git

### 1. Clonar o repositório

```bash
git clone https://github.com/IdeiaWare/IdeiaWare2026.git
cd IdeiaWare2026
```

### 2. Configurar o ambiente

```bash
cp .env.example .env
```

Edite o `.env` com os valores reais:

```env
DB_PASS=senha-forte-aqui
SENDGRID_API_KEY=sua-chave-sendgrid
CAIXA_HMAC_SECRET=valor-aleatorio-32-chars
```

### 3. Subir em produção

```bash
docker compose up -d --build
```

Acesso após a subida:
- LIC: `http://seu-servidor:8080/LIC`
- Toolkit: `http://seu-servidor:8080/toolkit/persona/lista`
- Login padrão: **admin / admin**

### 4. Subir em desenvolvimento (local)

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

Diferenças em dev: MySQL exposto na porta 3306, `hbm2ddl=update`, SQL logado no console.

### 5. Comandos úteis

```bash
# Ver logs em tempo real
docker logs ideiaware-outlook-app-1 -f

# Zerar o banco (apaga todos os dados)
docker compose down -v

# Parar tudo
docker compose down
```

> O primeiro boot inicializa o banco automaticamente com as 14 tabelas e o usuário admin/admin.
