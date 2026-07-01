# IdeiaWare + Toolkit — Docker

Sobe os 2 projetos (LIC `/LIC` + Toolkit `/toolkit`) num Tomcat 9 + MySQL 8, com `docker compose`.
**1 imagem** pros dois ambientes; a diferença dev/prod é só algumas variáveis.

## Estrutura esperada (monorepo)

```
ideiaware-outlook/            <- raiz (build context do Docker)
├── IdeiaWare-main/           <- LIC (já está aqui)
├── Toolkit2025-main/         <- Toolkit  ⚠️ FALTA MOVER PRA CÁ (ver passo 1)
├── app/   Dockerfile, setenv.sh, entrypoint.sh
├── db/    my.cnf, init/estrutura-lic_bd.sql
├── docker-compose.yml        <- base (PROD-safe)
├── docker-compose.dev.yml    <- override de dev
├── .env.example  /  .env (você cria)  /  .gitignore  /  .dockerignore
```

## Passo 1 — Mover o Toolkit pra dentro do monorepo (1x)

O Docker só enxerga o que está dentro da raiz. Mova a pasta do Toolkit (a que tem o `pom.xml`):

```
De:   C:\Users\Gustavo Motta\Desktop\Toolkit2025-main\Toolkit2025-main\
Para: C:\Users\Gustavo Motta\Desktop\ideiaware-outlook\Toolkit2025-main\
```

## Passo 2 — Criar o `.env`

```
cp .env.example .env
```
Edite o `.env`:
- `DB_PASS` — senha do banco (forte em prod).
- `SENDGRID_API_KEY` — **revogue a chave antiga** e cole a nova (ou deixe vazio = sem e-mail).
- `CAIXA_HMAC_SECRET` — troque por um valor aleatório próprio (32+ chars). Mesmo valor p/ os dois.

## Passo 3 — Subir

**Produção (servidor):** `git clone` + 
```
docker compose up -d
```

**Local/dev** (expõe o MySQL na 3306, hbm2ddl=update, mostra SQL):
```
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

No 1º boot o MySQL inicializa com `lower_case_table_names=1` + utf8mb4 + native_password e roda o
`estrutura-lic_bd.sql` (14 tabelas + admin/admin). O app espera o healthcheck do banco e sobe.

- Acesso: http://localhost:8080/LIC  e  http://localhost:8080/toolkit/persona/lista
- Login: **admin / admin**
- Zerar o banco do zero: `docker compose down -v` (apaga o volume).

## O que cada ambiente muda

| | Base (prod) | + dev override |
|---|---|---|
| MySQL porta 3306 | fechada (interno) | exposta |
| `HBM2DDL` | `validate` | `update` |
| `SHOW_SQL` | `false` | `true` |

## Como os segredos saem do código (#5)

Tudo vem do `.env` → variáveis do container:
- **Banco** (host/nome/senha/hbm2ddl/show_sql): o `app/entrypoint.sh` faz `sed` nos configs (`hibernate.cfg.xml` / `toolkit-servlet.xml`) na subida.
- **SendGrid / AES / HMAC**: o código lê direto via `System.getenv(...)` (com fallback p/ rodar local sem env).

> Pegadinhas mapeadas (D-1..D-7) já estão assadas: Tomcat 9 (D-1), `--add-opens` no `setenv.sh`
> (D-2), connector 8 (D-3), native_password (D-4), `lct=1` (D-5), charset+timezone (D-6), e o
> volume nomeado pro MySQL.
