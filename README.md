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

Edite o `.env`. Nem toda variável precisa ser preenchida — a tabela abaixo diz o que cada
uma faz e o que acontece se ficar com o valor padrão:

| Variável | Obrigatória? | Efeito se deixar em branco/default |
|---|---|---|
| `DB_NAME` | Não | Usa `lic_bd` (default já funciona) |
| `DB_PASS` | Recomendado trocar em produção | Default `root` — fraco pra um deploy real exposto |
| `HBM2DDL` | Não | Default `validate` (correto pra produção, não altera o schema) |
| `SHOW_SQL` | Não | Default `false` |
| `SENDGRID_API_KEY` | **Sim**, pra e-mail funcionar | Em branco = reset de senha e feedback silenciosamente não enviam e-mail (fica só no log) |
| `SENDGRID_FROM_EMAIL` | Só importa se `SENDGRID_API_KEY` estiver setada | Sem efeito sozinha |
| `FEEDBACK_EMAIL` | **Sim**, pro botão de Feedback funcionar | Em branco = feedback não é enviado (fica só no log) |
| `AES_KEY` | **Não usada** | Variável morta — nenhum código lê `AES_KEY` hoje (a classe que a usava foi removida numa limpeza anterior). Segura pra ignorar; candidata a ser removida do `.env.example`/`docker-compose.yml` numa faxina futura. |
| `CAIXA_HMAC_SECRET` | **Sim, crítico em produção** | Tem um fallback hardcoded no código-fonte (está no Git, é público) — se não trocar, o cookie `ideiaId` assinado entre LIC e Toolkit fica forjável |

> ⚠️ Antes de ir pra produção de verdade: (1) trocar `CAIXA_HMAC_SECRET` por um valor
> aleatório próprio (`openssl rand -hex 24`, por exemplo); (2) revogar/gerar uma chave nova
> do SendGrid, já que a antiga usada durante o desenvolvimento vazou no histórico do Git.

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
docker logs ideiaware2026-app-1 -f

# Zerar o banco (apaga todos os dados)
docker compose down -v

# Parar tudo
docker compose down
```

> O primeiro boot inicializa o banco automaticamente com as 14 tabelas e o usuário admin/admin.

---

## Rodar sem Docker (manual)

Útil pra quem não tem Docker disponível, ou quer rodar direto num servidor Tomcat já existente.

### Pré-requisitos

- **JDK 21** (o build/runtime oficial usado no Docker; versões mais novas do JDK também
  funcionam para compilar/testar, mas 21 é o que bate exatamente com o ambiente de produção)
- **Maven 3.9+**
- **Tomcat 9** (`javax.*`, não `jakarta.*` — não usar Tomcat 10+)
- **MySQL 8** (com `lower_case_table_names=1`, `utf8mb4`)
- Git

### 1. Clonar o repositório

```bash
git clone https://github.com/IdeiaWare/IdeiaWare2026.git
cd IdeiaWare2026
```

### 2. Criar o banco

```bash
mysql -u root -p -e "CREATE DATABASE lic_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p lic_bd < db/init/estrutura-lic_bd.sql
```

> O MySQL precisa estar configurado com `lower-case-table-names=1` (mesma flag usada no
> `docker-compose.yml`) — sem isso, os nomes de tabela em maiúscula das entidades Java
> (`Ideia`, `Usuario`, `Canva`...) não batem com as tabelas reais em minúsculo no Linux
> (case-sensitive por padrão). No `my.cnf`/`my.ini`:
> ```ini
> [mysqld]
> lower_case_table_names=1
> ```

### 3. Configurar a conexão com o banco

Os 2 projetos têm a conexão hardcoded nesses arquivos — edite ANTES de compilar se o banco
não estiver em `127.0.0.1:3306` com usuário `root`/senha `root` (os valores default já
funcionam pra um MySQL local recém-instalado, sem precisar editar nada):

- `IdeiaWare-main/src/main/resources/hibernate.cfg.xml` — `connection.url` /
  `connection.username` / `connection.password`. Pra produção, também troque
  `hbm2ddl.auto` de `update` pra `validate` (o schema já foi criado pelo passo 2, não
  precisa que o Hibernate altere tabelas em produção) e `show_sql` pra `false`.
- `Toolkit2025-main/WebContent/WEB-INF/toolkit-servlet.xml` — `jdbcUrl` / `password` (mesma
  seção `myDataSource`); `hibernate.show_sql` pra `false` em produção.

### 4. Configurar os segredos (variáveis de ambiente)

`SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL`, `FEEDBACK_EMAIL` e `CAIXA_HMAC_SECRET` são lidos
via `System.getenv()` — precisam existir como variável de ambiente do PROCESSO do Tomcat
(não é algo que dá pra editar num XML). O jeito mais simples é criar `bin/setenv.sh`
(Linux/Mac) ou `bin/setenv.bat` (Windows) dentro da instalação do Tomcat — ele é lido
automaticamente pelo `catalina.sh`/`catalina.bat` no start:

```bash
# $CATALINA_HOME/bin/setenv.sh
export SENDGRID_API_KEY="sua-chave-sendgrid"
export SENDGRID_FROM_EMAIL="seu-remetente@dominio.com"
export FEEDBACK_EMAIL="destino-do-feedback@dominio.com"
export CAIXA_HMAC_SECRET="um-valor-aleatorio-bem-grande"
export CATALINA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED"
```

> ⚠️ `CAIXA_HMAC_SECRET` tem um valor hardcoded de fallback no código-fonte (usado se a env
> var não estiver setada) — em produção, SEMPRE setar essa variável com um valor próprio.
> Sem isso, a chave que assina o cookie `ideiaSig` fica pública (está no Git), tornando o
> cookie forjável.
>
> `AES_KEY` existe no `.env.example` mas não é lida por nenhum código atual (variável morta,
> sobra de uma classe de criptografia removida numa limpeza anterior) — não precisa setar.

### 5. Preparar as pastas de dados

O app grava fora do diretório do WAR (sobrevive a redeploys) — crie e dê permissão de
escrita ao usuário que roda o Tomcat:

```bash
sudo mkdir -p /data/ideiaware-exports
sudo chown -R tomcat:tomcat /data/ideiaware-exports
```

> Caminho customizável via a system property `-Dideiaware.exports.dir=/outro/caminho`
> (mesmo `CATALINA_OPTS` do passo 4). No Windows, sem configurar nada, isso vira
> `C:\data\ideiaware-exports` (raiz do drive atual).

### 6. Compilar e gerar os WARs

```bash
mvn -f IdeiaWare-main/pom.xml   clean package -DskipTests
mvn -f Toolkit2025-main/pom.xml clean package -DskipTests
```

Gera `IdeiaWare-main/target/LIC.war` e `Toolkit2025-main/target/toolkit.war`.

### 7. Fazer o deploy no Tomcat

```bash
cp IdeiaWare-main/target/LIC.war       $CATALINA_HOME/webapps/
cp Toolkit2025-main/target/toolkit.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```

O Tomcat extrai e sobe os 2 WARs automaticamente (contexto `/LIC` e `/toolkit`, pelo nome
do arquivo). Acesso:
- LIC: `http://seu-servidor:8080/LIC`
- Toolkit: `http://seu-servidor:8080/toolkit/persona/lista`
- Login padrão: **admin / admin**

### Rodando local pra desenvolvimento (sem Docker)

Mesmos passos 1-5 acima, mas: não mexa no `hibernate.cfg.xml`/`toolkit-servlet.xml` se o
MySQL local já for `127.0.0.1:3306` root/root (é o default dos arquivos); deixe
`hbm2ddl.auto=update` e `show_sql=true` (já vem assim) pra ver o SQL no console e deixar o
Hibernate ajustar o schema sozinho durante o desenvolvimento. Pode rodar direto pela IDE
(deploy do WAR num Tomcat local/embutido) em vez dos passos 6-7 manuais.
