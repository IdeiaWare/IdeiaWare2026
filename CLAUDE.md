# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

IdeiaWare is an academic Design Thinking / co-creation platform made of **two independent Maven WAR projects** that share one MySQL database and one Tomcat instance:

- **`IdeiaWare-main`** (context `/LIC`) — idea management: registration, collaboration, storytelling, canvas, export. Servlet + JSP + Hibernate, package root `edu.unisc.lic`.
- **`Toolkit2025-main`** (context `/toolkit`) — Design Thinking tools bound to a LIC idea (Persona, Point of View, Empathy Map). Spring MVC + Hibernate, package root `br.unisc.toolkit`.

There is no parent/aggregator POM — the two modules build, test and version independently, and never import each other's code. They integrate only at runtime (shared DB, shared disk, signed cookie).

Stack: Java (source level 1.8, JDK 21 runtime), Tomcat 9 (`javax.*` — **not** Tomcat 10+/`jakarta.*`), MySQL 8, Hibernate 5.6, Spring MVC 5.3 (Toolkit only), Materialize, Maven. Exact versions and the divergences between the two projects are in "Versions and toolchain" below.

## Commands

### Docker (runs the whole stack)

```bash
cp .env.example .env                                   # see README.md for what each var does
docker compose up -d --build                           # prod-like: both WARs in one Tomcat + MySQL
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build   # dev: MySQL on 3306, hbm2ddl=update, SQL logged
docker logs ideiaware2026-app-1 -f
docker compose down -v                                 # wipes the DB
```

LIC: `http://localhost:8080/LIC` · Toolkit: `http://localhost:8080/toolkit/persona/lista` · default login `admin`/`admin`.

### Build and test (per module, from repo root)

```bash
mvn -f IdeiaWare-main/pom.xml   clean package -DskipTests
mvn -f Toolkit2025-main/pom.xml clean package -DskipTests

mvn -f IdeiaWare-main/pom.xml   test          # 368 tests
mvn -f Toolkit2025-main/pom.xml test          # 203 tests

mvn -f IdeiaWare-main/pom.xml test -Dtest=AssinaturaCaixaTest
mvn -f IdeiaWare-main/pom.xml test -Dtest=AssinaturaCaixaTest#nomeDoMetodo
```

Both POMs run JaCoCo during `test` (`target/site/jacoco/index.html`). The Dockerfile runs the full suite during the image build — a failing test blocks the image.

Those counts are measured, not estimated: `mvn test` on 2026-09-02 reported `Tests run: 368, Failures: 0, Errors: 0, Skipped: 0` for LIC and `Tests run: 203, ... Skipped: 0` for the Toolkit, both BUILD SUCCESS. Nothing is `@Ignore`d — the `@Ignore` strings in LIC test files are inside comments recording that those tests were reactivated (`TEST-04`). Both suites also pass on a JDK newer than the Docker image (verified on JDK 25), so a local run does not require JDK 21.

DAO tests run against in-memory **H2**, configured per module in `IdeiaWare-main/src/test/resources/hibernate.cfg.xml` and `Toolkit2025-main/test/resources/applicationContext-test.xml`.

Non-standard layout in `Toolkit2025-main`: sources are in `src/` (not `src/main/java`), tests in `test/`, web root in `WebContent/` — all wired explicitly in its POM.

Manual (non-Docker) deploy — DB creation, connection strings, env vars, exports dir, WAR copy — is documented step by step in `README.md`.

## Versions and toolchain

**Language level is Java 8, but nothing runs on a Java 8 JVM.** Both POMs pin `source`/`target` to `1.8` while the build and runtime images are JDK 21. Write Java 8-compatible code (no `var`, no records, no `List.of`), but expect JDK 21 behaviour at runtime — that mismatch is why the `--add-opens` flags exist.

| Layer | Version |
|---|---|
| Build image | `maven:3.9-eclipse-temurin-21` |
| Runtime image | `tomcat:9.0-jdk21-temurin` |
| Database image | `mysql:8.0` |
| Java source/target | 1.8 |
| Servlet API | `javax.*` — Toolkit compiles against `javax.servlet-api 4.0.1` (provided); LIC against `javaee-web-api 7.0` (provided), plus `javax.servlet-api 4.0.1` test-scoped so `Cookie` has real method bodies (`TEST-04`) |

Backend libraries — Hibernate `5.6.15.Final` and MySQL Connector/J `8.0.33` in **both** projects; Spring `5.3.39` (webmvc/orm/context-support/test), `hibernate-c3p0 5.6.15` and `hibernate-validator 6.2.5` in the **Toolkit only**; jBCrypt `0.4`, SendGrid `4.9.3`, Gson `2.10.1`, Guava `32.1.3-jre`, commons-io `2.11.0`, commons-fileupload `1.5`, commons-collections `3.2.2` in **LIC only**. Tests: JUnit `4.13.2`, Mockito `3.12.4`, H2 `1.4.200`, plus `spring-test 5.3.39` on the Toolkit side.

Divergences between the two POMs that are easy to trip over:

- **Connection handling differs.** The Toolkit pools with c3p0 (`ComboPooledDataSource`, min 5 / max 20 / `maxIdleTime` 30000). LIC configures no pool, so Hibernate uses its built-in `DriverManagerConnectionProvider` and each DAO call takes a fresh JDBC connection — which is what session-per-DAO-method costs in practice. Budget queries accordingly on the LIC side.
- `hibernate-validator` is declared by the Toolkit but **never used** — there is no `@Valid`, `@NotNull` or any constraint annotation in the codebase. Validation is hand-written (`ToolkitValidacao`, inline servlet checks).
- JSTL comes from different coordinates: LIC uses the legacy `jstl:jstl:1.2`, the Toolkit uses the Glassfish `javax.servlet.jsp.jstl` artifacts with exclusions.
- The SendGrid dependency's groupId is written `com.sendgrid.` — **with a trailing dot**. It resolves, so leave it alone unless you are deliberately fixing it and can re-verify the build.
- Maven plugins differ: compiler `3.3` (LIC, from 2015) vs `3.8.1` (Toolkit); `maven-war-plugin 3.3.2` pinned only by the Toolkit because it needs `warSourceDirectory=WebContent`; JaCoCo `0.8.13` on both.
- **LIC pins surefire `3.2.5` with an explicit `argLine` that starts with `@{jacocoArgLine}`.** That late-bound property is what attaches the JaCoCo agent — if you add or override `argLine` and drop it, tests still pass but coverage reports come out empty. The Toolkit does not pin surefire and relies on JaCoCo's default `argLine` injection.

Frontend versions (all committed to the repo, no npm, no build step; ~2.0 MB of JS in LIC and ~4.4 MB of assets in the Toolkit):

| Library | LIC | Toolkit |
|---|---|---|
| Materialize | **0.100.1** | **0.99.0** |
| jQuery | 3.2.1 | 3.2.1 |
| jsPDF | 1.0.272-git (2014) | own `jspdf.min.js` copy |
| Konva | 10.3.0 | — |
| Quill | 1.3.2 | — |
| html2canvas | — (replaced by native canvas, `EXP-CAN`) | 0.5.0-beta3 |
| Font Awesome | 4.7.0 via CDN | 4.7.0 local |
| Other | sprintf 1.1.1, recorder.js, base64.js | masonry 4.2.0, typeahead 0.11.1, enjoyhint, materialize-tags |

**The two apps run different Materialize majors** (0.100.1 vs 0.99.0) against the same visual design. That is a standing source of small UI differences between LIC screens and Toolkit screens, and it means a CSS fix verified in one app is not automatically valid in the other.

## How Docker runs it

`app/Dockerfile` is a two-stage build and the compose file wires it to MySQL:

1. **Build stage** copies *only the two `pom.xml` files* first and runs `dependency:go-offline`, so editing source does not re-download dependencies (`BUILD-CACHE`). Then it copies the sources and runs `clean package` for each project. `SKIP_TESTS` is a build ARG defaulting to `false`, so **the full test suite gates the image** — a failing test means no image.
2. **Runtime stage** starts from Tomcat 9, deletes `webapps/*` (so there is no ROOT, manager or host-manager — `http://host:8080/` returns 404; you must go to `/LIC` or `/toolkit`), then **explodes** both WARs with `jar -xf` into `webapps/LIC` and `webapps/toolkit`. Exploding rather than dropping `.war` files is deliberate: it lets the entrypoint edit the config files inside the deployed apps before Tomcat starts.
3. `sed -i 's/\r$//'` normalizes line endings on the two shell scripts, because the repo is developed on Windows and CRLF would break them under Linux. Keep that in mind if you add another script.
4. **`entrypoint.sh` rewrites `hibernate.cfg.xml` and `toolkit-servlet.xml` with `sed`** — DB host/port/name, password, `hbm2ddl`, `show_sql` — then `exec "$@"` hands off to `catalina.sh run`. `setenv.sh` contributes only the `--add-opens` flags via `CATALINA_OPTS`.

Compose-level integration details that matter:

- MySQL server flags are passed on the `command:` line: `--lower-case-table-names=1` (required by the naming strategy), utf8mb4 charset and collation, and `--default-authentication-plugin=mysql_native_password` for driver compatibility. **`lower_case_table_names` can only be set when the data directory is initialized** — changing it later means recreating the `dbdata` volume.
- The app waits on `condition: service_healthy`, not merely "started"; the healthcheck is `mysqladmin ping` every 10s, 10 retries.
- `./db/init` is mounted read-only into `/docker-entrypoint-initdb.d`, so the schema script runs only against an empty `dbdata` volume.
- Three named volumes: `dbdata`, `storytelling_images` (mounted *inside* the exploded LIC webapp) and `exports_data`. **Nothing sets `-Dideiaware.exports.dir`** — the container works because the volume is mounted at `/data/ideiaware-exports`, which happens to be `Constantes.caminhoExports()`'s hardcoded default. Change one and you must change the other.
- `docker-compose.dev.yml` overrides exactly three things: it publishes MySQL on 3306, flips `HBM2DDL` to `update` and `SHOW_SQL` to `true`.

## Architecture

### LIC — servlet-per-action

- `servlet/` — one `HttpServlet` per user action (~58), each mapped individually in `web.xml`. No front controller or router.
- `dao/` — one DAO per entity extending `GenericDAO<T>`. **Every DAO method opens and closes its own Hibernate `Session`** (no Open-Session-In-View). This session-per-method pattern is deliberate; changing it would touch every DAO, and was evaluated and deferred. Don't refactor it opportunistically.
- `domain/` — Hibernate entities (10, listed explicitly in `hibernate.cfg.xml` — a new entity must be registered there).
- `classes/` — cross-cutting helpers: email, PDF export, date formatting, HMAC signing, reset tokens, and `StatusIdeia` (the status code constants — use these, not string literals).
- `util/` — the servlet `Filter`s plus `HibernateUtil` and the lowercase naming strategy.

### Toolkit — layered Spring MVC

`controller → service → repository → entity`, wired by `context:component-scan` in `WebContent/WEB-INF/toolkit-servlet.xml` (XML-configured Spring, **not** Spring Boot). Views resolve from `/WEB-INF/view/*.jsp`. `classes/` holds the same category of helpers as LIC's `classes/`, duplicated per project rather than shared as a library.

### The two apps share state outside their WARs

- **Same MySQL schema** (`lic_bd`, 14 tables, `db/init/estrutura-lic_bd.sql`), each project with its own Hibernate config. Requires `lower_case_table_names=1` — entity-derived names are PascalCase, real tables are lowercase.
- **Duplicated entities**: `Ideia` and `Usuario` exist in *both* projects, mapping the *same* tables. Changing one side does not change the other, and they have already drifted (LIC `Ideia.descricao` is `length=1500`, matching the real schema; Toolkit's says `200`). Harmless today only because Toolkit sets no `hbm2ddl.auto` and never touches the schema — but check both sides when changing a shared column.
- **Files on disk** live in two different places with different guarantees: exported PDFs *outside* the WAR under `ideiaware.exports.dir`, Storytelling images *inside* it. Each project resolves the exports path through its own `Constantes` class. See "Export and file storage" for the full table.

### LIC → Toolkit handoff

`EntrarCaixaServlet` (LIC) is the bridge: it checks login and that the user actually participates in the idea, then sets cookies `ideiaId` + `ideiaSig` with `path=/` so both contexts see them. Toolkit reads them via `AdminCookies` and rejects the id unless the HMAC matches.

`AssinaturaCaixa` exists twice (`edu.unisc.lic.classes` signs, `br.unisc.toolkit.classes` signs *and* validates) and **both must stay algorithmically identical and use the same `CAIXA_HMAC_SECRET`**. Change one, change the other. There is a hardcoded fallback secret in source — public and forgeable, local dev only.

## Database

One MySQL schema (`lic_bd`, 14 tables) shared by both apps, created by `db/init/estrutura-lic_bd.sql`.

### Dependency graph

`usuario` is the only root; **`ideia` is the hub** — every other table reaches it directly or transitively, making `ideia_codigo` the de-facto tenancy key.

```
usuario
  ├──< ideia.usuario_codigo  NOT NULL  (author)
  └──< ideia.gestor_codigo   NULL      (manager who validated it)
          │
          │  LIC side — no cascade
          ├──< ideiausuario (→usuario)   UNIQUE(usuario,ideia)
          ├──< logcolaboracao (→usuario)
          ├──< colaboracaoideia (→usuario)
          ├──< storytelling (→usuario)   UNIQUE(ideia)
          │       └──< elementosstorytelling
          ├──< canva
          ├──< canvaexport               UNIQUE(ideia)
          ├──< export_file               shared by LIC + Toolkit
          │
          │  Toolkit side — ON DELETE CASCADE
          ├──< persona ──< empathy (→persona, →ideia)
          ├──< pov
          └──< persona_pov (→persona, →pov, →ideia)  UNIQUE(persona,pov)
```

### Two ownership models coexist

LIC models the idea relationship as real JPA (`@ManyToOne`/`@OneToOne`). **Toolkit does not**: its entities carry a plain `private Long ideiaCodigo` scalar, so the foreign key exists only in the DDL, never in the object model. Every Toolkit query therefore has to pass `ideiaCodigo` by hand (`from Persona where ideiaCodigo=:IdeiaCodigo`, `delete from PointOfView where id=:ID and ideiaCodigo=:ideiaCodigo`). Omitting that filter in a new Toolkit query is a silent IDOR — the ORM provides no safety net there.

### No cascades anywhere in Java

There is not a single `cascade`/`orphanRemoval` attribute and not a single `@OneToMany` in either project: all relations are unidirectional child→parent. Combined with `ON DELETE CASCADE` existing only on the Toolkit tables, deleting an `ideia` would clean up persona/pov/empathy and then fail on FK constraints for every LIC table.

This is inert today because **nothing deletes an idea or a user**. The only `excluir()` calls are on leaves (canva post-it, storytelling element, export file). The LGPD path anonymizes in place instead — `Usuario.AnonimizaDadosPessoais()` overwrites name/login/email, sets `anonimizado='S'` and resets the password, keeping foreign keys and collaboration history intact. Assume deletion is not a supported operation unless you add the cascade story yourself.

### UNIQUE constraints are race-condition fixes

Six UNIQUE constraints exist specifically to close double-submit windows that the Java-side checks could not (each is documented inline in the SQL). Don't drop them as "redundant validation": `uk_usuario_login`/`uk_usuario_email` (simultaneous signup), `uk_ideiausuario_par` (double-click "Entrar"), `uk_storytelling_ideia` (enforces the `@OneToOne`), `uk_canvaexport_ideia` (concurrent export), `uk_persona_pov_persona_pov` (double-click "Salvar" on the POV form).

### Schema evolution is manual

The script is idempotent (`CREATE TABLE IF NOT EXISTS`) but it is **not a migration tool**, and MySQL only runs it on a first boot against an empty volume. An existing database gains no new column or constraint on its own — the required `ALTER TABLE` statements are written as comments next to each affected table (unique keys, the group-waitlist columns, the reset-token columns). There is no Flyway/Liquibase: when you change the schema, add the DDL *and* the manual ALTER note.

### Schema/entity gotchas

- The duplicated entities are maintained independently and are not guaranteed to agree — `Ideia.descricao` is `varchar(1500)` in the DB and in LIC's mapping, `length=200` in the Toolkit's. The Toolkit declares no `hbm2ddl.auto` and never emits DDL, so its mappings are read-only descriptions of a schema LIC owns. Check both sides when changing a shared column.
- `LowerCaseTableNamingStrategy` lowercases **table** names only, which is why columns stay camelCase (`dtCriacao`, `flLider`) while tables are lowercase. Each project has its own copy of the class.
- `pov.user` collides with a MySQL reserved function name; it works only because native queries always qualify it (`pov.user`).
- Toolkit's own tables use `int` PKs with custom names (`persona_id`, `pov_id`); LIC uses `Long`/`bigint` `codigo` from `GenericDomain`. Only `ideia_codigo` is `bigint` on both sides, which is what matters.
- Performance indexes are deliberately few: `idx_ideia_status`, `idx_exportfile_tipo`, and the composite `idx_empathy_lookup (fk_persona_id, attribute, ideia_codigo)` which matches `EmpathyDAOImpl`'s query exactly — keep them in sync if that query changes.

## Idea lifecycle (the core state machine)

`ideia.status` drives what every module allows. Codes live in `StatusIdeia` (LIC) — use the constants, never string literals. **The machine spans both applications**, which is easy to miss:

```
CadastroIdeiaServlet        → PE  Pendente
ValidarIdeiaServlet         → VA  Validada   |  RE Rejeitada (terminal)
FecharGrupoServlet          → DE  Em desenvolvimento
FinalizarColaboracaoServlet → ST  Storytelling
ExportaStoryServlet         → CF  Caixa de Ferramentas   (also sets storytelling.status = FN)
IdeiaDAOImpl.finalize()     → CV  Canvas        ←── lives in the TOOLKIT, not LIC
ExportCanvaServlet          → FN  Finalizado (terminal)
```

The `CF → CV` transition is performed by the Toolkit (`br.unisc.toolkit.repository.IdeiaDAOImpl.finalize`, reached from `IdeiaController#finalizeIdeia`). LIC deliberately never writes `CV` — `IdeiaUsuarioDAO` carries a `RET-CV` comment saying so. If you change the lifecycle, check both apps.

Each module also guards its own step: writing is only allowed while the idea sits on that module's status. LIC servlets check it inline; Toolkit centralizes it in `StatusGuard.podeEscrever(...)`. Both then follow the same UX contract — a message plus a redirect through an intermediate page (`/aviso-etapa-encerrada` in the Toolkit) rather than a silent failure.

Group membership is a second, independent state: `ideia.statusGrupo` (`AB`/`FE`) plus `ideiausuario.flStatusVinculo` (`P`endente/`A`provado/`R`ejeitado) for the waiting list. **`flStatusVinculo IS NULL` is treated as approved** — rows predate the waitlist feature.

## Export and file storage

PDFs are **generated in the browser, not on the server**. The server only receives, decodes and stores them:

1. Client builds the image — Storytelling uses Konva `stage.toDataURL()`, Canva draws on a native 2D canvas (`EXP-CAN`, deliberately replacing an html2canvas screenshot), Toolkit still uses html2canvas.
2. It is repainted onto a white canvas and re-encoded as **JPEG 0.8** (`STM-24`) — PNG used to blow MySQL's max packet back when PDFs lived in the database.
3. jsPDF composes an A4 landscape document, paginating by hand in a loop.
4. `pdf.output("blob")` → `FileReader` → data URI → POST to the servlet.
5. `ArquivoExport.salvar()` decodes the base64 and writes **atomically** (temp file + `ATOMIC_MOVE`, `CONC-01`) so a crash mid-write cannot leave a corrupt PDF.
6. `AbrirPDF.abrir()` streams it back with a sanitized `Content-disposition` filename.

Three storage destinations, with different guarantees:

| What | Where | Survives redeploy? |
|---|---|---|
| Exported PDFs | `ideiaware.exports.dir` (default `/data/ideiaware-exports`), **outside** the WAR | yes, by design |
| Storytelling images | `getServletContext().getRealPath("")` + `imagensStorytelling/{ideiaCodigo}/`, **inside** the WAR | only because `docker-compose.yml` mounts a volume there |
| Storytelling **audio** | not on disk at all — base64 inside `elementosstorytelling.caminho` | yes |

`caminho` is an overloaded column and the distinction matters when you read or write it: for `IMG` elements it holds a **path**, for `AUD` elements it holds the **base64 content itself** (`AUDIO-DATAURI`). Audio is one slot per storytelling by design — the schema stores a single `AUD` row and `substituirAudio` swaps it (delete old + insert new) in one transaction (`K.8 #7`), with the editor UI collapsing its list to match (`UX-STORYTELLING-AUDIO-1-SLOT`).

The two apps name their exports differently, and it changes behaviour:

- **LIC writes deterministic paths** (`storytelling/{ideiaCodigo}.pdf`, `canva/{ideiaCodigo}.pdf`), so a re-export overwrites — hence the server-side block on re-finalizing.
- **Toolkit writes `conhecimento/{ideiaCodigo}/{uuid}.pdf`**, so re-exporting accumulates files instead. Its `ExportFile.fileLocation` is reused in both directions: the form posts the base64 payload in that field, and `ArquivoExport.salvar` returns the real relative path which overwrites it before the row is persisted.

Uploads are validated by extension allowlist, stored under a random UUID name, then re-read with `ImageIO` to confirm the bytes really are an image; failure deletes the file (`SEC-12`).

## Runtime contracts

**Session attributes** (LIC) — no constants class, these are string keys used across servlets and JSPs: `codigoUsuario` (the login marker, ~74 uses), `ideiaId` (idea currently open), `storytellingId` (storytelling being edited), `ideiaTitulo`/`ideiaDesc`, `lider`, `mensagemErroEtapa` (the stage-lock message), plus the nine `canva-*` block keys. Servlets re-read `ideiaId`/`storytellingId` from the session rather than trusting a request parameter — keep that.

**Cookies** — `ideiaId` + `ideiaSig` (HMAC) and `usuarioNome` are set with `path=/` so both webapps see them; `XSRF-TOKEN` is scoped to the context path.

**JSON responses must use `JsonUtil.GSON_SEM_SENHA`**, never a bare `new Gson()`. It excludes `Usuario.senha` during serialization; entities reach these endpoints with the user attached, so a plain Gson would put the bcrypt hash on the wire.

**Collaboration is polled, not pushed.** `colaboracao.jsp` keeps a local `ultimoCodigo` and posts it to `RetornaMensagensServlet` on an interval; the servlet returns only rows with a greater id. Consequences worth knowing: only *new* collaborations appear live — edits to existing ones do not — and an SSE prototype was built, tested and **explicitly rejected**, so don't reintroduce push without re-confirming that decision.

**Rate limiting is per-JVM and in memory** (a static `ConcurrentHashMap` in `RateLimitFilter`, 10 attempts / 2 min per IP+endpoint on login, signup and password reset). It resets on restart and does not coordinate across instances.

**Password reset** stores only `SHA-256(token)` in `usuario.resetTokenHash` with a 1h expiry; the clear token travels solely in the emailed link. Passwords themselves are bcrypt (`Usuario.checaSenha`), never SHA-256 — that migration is done.

**Flash messages (LIC)** use a one-shot session attribute: a servlet sets `mensagemErroEtapa` and redirects; `headerCookies*.jsp` reads it, immediately removes it, and emits `alert(<%= new Gson().toJson(msg) %>)`. Encoding through Gson is what makes injecting a server string into a `<script>` block safe — keep that if you add new flash messages. The Toolkit's equivalent is the `/aviso-etapa-encerrada` redirect hop with `RedirectAttributes` flash attributes.

## Module notes

**Canva (9 blocks).** The nine "enter block X" servlets are thin subclasses of `EntrarCanvaBaseServlet` (`MNT-01`); each only overrides `getTipoCanva()` and `getPaginaDestino()`. Adding a tenth block means: one subclass with those two methods, a `web.xml` mapping, and a JSP. `EntrarCanvaServlet` is a separate, non-subclass entry point: it establishes the board session state (`ideiaId`, `ideiaTitulo`, `lider`, `isRetencao`) and preloads all nine block lists at once. Post-it colours and text go through `EnviarCanvaServlet`, which validates `text.trim().length() >= 5` **server-side** as a backstop (`UX-CANVA-VALIDACAO-SERVIDOR`) because that rule used to live only in the browser — and the submit button is enabled by text length, not by having clicked a colour, which was the cause of the permanently-stuck button.

**Retention mode (`isRetencao`).** The knowledge-retention module does not have its own screens: it reuses the Canva and Colaboração pages in read-only mode. `EntrarCanvaServlet`/`EntrarColaboracaoServlet` set the session flag from a `retencao` request parameter, and the JSPs hide every editing control behind `<c:if test="${isRetencao eq false}">`. A new editing control on those pages must be wrapped the same way or it becomes reachable in retention mode.

**Roles.** There are only two permission values: `adm` and `col`, stored in `usuario.permissao` (`varchar(3)`). The management UI labels `adm` as "gestor", and `PermissaoUsuarioServlet` toggles between the two. `ideia.gestor_codigo` is unrelated to the role — it records which admin validated that specific idea.

**Email is best-effort and never blocks the flow.** `EnvioEmail.EnviaEmail` returns `false` and logs when `SENDGRID_API_KEY` is missing or SendGrid rejects the send; callers surface a message but do not fail. Three call sites: password reset, "send me my personal data" (LGPD), and the feedback button. The from-address falls back to a hardcoded `@outlook.com` address, which is exactly the sender with the known Gmail deliverability problem documented in `RELATORIO-FINAL.md`.

**Collaboration → description merge.** Members post `ColaboracaoIdeia` rows; only the **leader** (`ideiausuario.flLider = 'S'`) folds them into the idea's running description via `AddDescricaoServlet` (`SRV-IDOR-04`), which appends to `LogColaboracao` — that table, not `ideia.descricao`, is the description history. `flSalvado = "ad"` marks a collaboration as already merged and doubles as the idempotency guard against a double POST (`K.8 #8`). A collaboration can be edited **only by its author and only before it was merged** (`M.10`).

**Group and waiting list.** Joining creates an `ideiausuario` row with `flStatusVinculo = 'P'`; the leader approves or rejects it. Only the leader can close the group (`SEC-13`), and closing is what flips the idea to `DE` — group close and status change are persisted together by `fecharGrupoAtomico` (`K.8 #1`). Note that `FecharGrupoServlet` calls `listarParametro(new IdeiaUsuario(null, ideia, "S"))` with a **null user on purpose** to find "the leader, whoever it is" — this is the production caller that makes `IdeiaUsuarioDAO`'s fail-open behaviour load-bearing.

**Empathy Map: six near-identical controllers.** `See`, `Hear`, `SayDo`, `ThinkAndFeel`, `Pain` and `Gain` are ~112-line copies of each other with **no shared base class**, differing only in URL (`/o-que-ve`, `/o-que-escuta`, …), page title and attribute name. Any behavioural change has to be repeated six times — this duplication is why past fixes landed in five of six places and had to be swept afterwards. Contrast with LIC's Canva servlets, where the same problem was solved with a base class (`MNT-01`); the Toolkit was never given the same treatment.

**Rich text (Quill) is stored as HTML.** The safety of that path rests on two places, not on a sanitizer: the Quill toolbar is restricted to bold/italic/underline/strike, size, colour and sub/superscript (no links, images or lists), and every render goes through JSTL `<c:out>`, i.e. escaped. `SalvarTextoServlet.retornaTextoFormatado` only strips the outer `<p>` wrapper and turns `</p>` into `<br>` — it sits where a sanitizer would, but it is a formatter. Widening the toolbar or rendering any of that HTML raw means adding server-side sanitization as part of the same change.

**Storytelling editor (Konva).** `controle.js` (431 lines) builds a single `Konva.Stage` → one `Konva.Layer` named `camadaImagens` → one shared `Konva.Transformer` for select/resize. Elements are `IMG`, `TXT` or `AUD` rows in `elementosstorytelling`. `controleAdd.js` draws and wires each shape; `controleAudio.js` owns the microphone. Every drag/resize/edit POSTs the changed element as a one-item JSON array, and the header "Salvar" flushes the whole array through `AutoSalvarStoryServlet`.

**Element placement is decided by the client.** `proximaPosicaoLivre()` picks a slot by retrying `proximaPosicao()` against `posOcupadas`, an in-page array seeded from `RetornaElementos` on load, and `InserirForma` persists whatever `x`/`y` arrives — the server never allocates or offsets a position. Placement is therefore only as coordinated as a single page's view of the board: two tabs open on the same storytelling reason from separate arrays. If you need placement to be authoritative, it has to move to the server; the reservation is synchronous within a page for both element types (`AUDITORIA-2026-07-16`).
**Guided tour (Toolkit only).** `enjoyhint` drives a multi-page onboarding tour, loaded by the `footer.tag` / `no-container-footer.tag` layout tags on every Toolkit page, with steps defined in `custom.tour.js`. Progress is carried **in cookies**, not in the session: `tourStep` (1–5, which part to resume) and `skipedTour` (set when the user skips), both 1-day. That is why the tour survives navigation between controllers. Clearing cookies replays it.

**Module colours are Materialize class names on the index cards**, one per module: Colaboração `teal lighten-2`, Storytelling `indigo lighten-1`, Caixa de Ferramentas `red darken-3`, Canvas `blue darken-4`, Retenção do Conhecimento `light-blue darken-1`. Screens inside a module are expected to carry its colour; that consistency was restored by hand after several UI passes, so match the module rather than picking a new shade.

**Cross-app link.** The Toolkit reaches back into LIC through the `licBasePath` context-param in its `web.xml` (default `/LIC`, centralized by `DRY-LIC` after being hardcoded in six places). `Constantes.paginaMinhaIdeia()` builds the return URL from it. If either context path changes, that param is the single place to update.

**The POV `names` field crosses three layers.** Every server-side render in `list-point-of-view.jsp` escapes it with `<c:out>`, including the `data-*` attributes. `buildPOVInfoEditOnModal` then puts values into form inputs with `.val()` (no HTML parsing) and hands the persona names to the third-party **materialize-tags** plugin, which renders them as chips — the one hop in that chain whose escaping is the plugin's, not the app's. Keep new POV rendering on the `<c:out>` + `.val()` path.

**Git conventions are loose.** Everything lives on `main` (22 commits, no feature branches in history). Messages mix Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`) with plain Portuguese descriptions — both styles are already in the log, so follow whichever fits, but prefer the prefixed form for anything substantial.

## Conventions to follow when adding code

- **Auth is not filter-based.** There is no authentication filter in `web.xml`. Protection is per-page and per-endpoint:
  - JSP pages include one of `header/headerCookies*.jsp`, which redirects to `login.jsp` when the session has no `codigoUsuario`. A new page that includes none of them is publicly reachable. (The four variants each load a different module bundle: `headerCookies.jsp` is the plain one, `headerCookies_2.jsp` is the Storytelling editor, `headerCookiesCV.jsp` is Canva, `headerCookiesColaboracao.jsp` is Colaboração — pick the one whose scripts your page needs.)
  - Servlets re-check `session.getAttribute("codigoUsuario")` themselves, then check that the user participates in the idea being touched (tagged `SRV-IDOR-*`). Copy that guard into any new servlet.
- **CSRF** is double-submit cookie (`CsrfFilter` / `CsrfInterceptor`): the `XSRF-TOKEN` cookie must match the `csrfToken` request parameter or `X-CSRF-Token` header on every POST. New forms and fetch calls must send it.
- **`PostOnlyFilter` is a hand-maintained allowlist** of write endpoints that reject GET. A new write servlet must add itself to the set or it silently keeps accepting GET.
- **DAO list methods are fail-closed** (`DAO-FAIL-CLOSED`): with no idea/owner set, return nothing rather than listing everything. `GenericDAO` has no shared helper for this, so each new DAO must implement the guard by hand.
  - **Exception — do not "fix" these:** `IdeiaUsuarioDAO`, `StorytellingDAO` and `LogColaboracaoDAO` are deliberately fail-*open* in `listarParametro` ("null means don't filter"). Real callers (`FecharGrupoServlet`, `EntrarStorytellingServlet`) depend on it; tightening them breaks those screens.
- **Stage lock**: any new write path must refuse to run once the idea has left its module's status, and must fail loudly (message + redirect), not silently. See "Idea lifecycle" above.
- **Toolkit queries must carry `ideiaCodigo` explicitly** — its entities have no JPA relationship to `Ideia`, so a missing filter is a silent IDOR. See "Database" above.

## Testing

Both suites run on **H2 in memory**, and both configs exist only to shadow production config on the test classpath (`src/test/resources/hibernate.cfg.xml` in LIC wins over the real one because `test-classes` precedes `classes`).

Things that will bite you when writing a new test:

- **The two projects isolate tests in opposite ways — match the one you are in.** All 6 Toolkit DAO test classes are `@RunWith(SpringJUnit4ClassRunner)` + `@ContextConfiguration("classpath:applicationContext-test.xml")` + `@Transactional`, so Spring rolls each test back and the database stays clean. The 11 LIC DAO test classes are plain JUnit with **no `@Before`/`@After` cleanup at all** — the H2 database is created once (`DB_CLOSE_DELAY=-1`, `hbm2ddl=create`) and data accumulates across the whole run. Every one of those 11 classes therefore generates unique values (`"autor_canva_" + System.nanoTime()` for login and email) because the real UNIQUE constraints are active. A new LIC test with a hardcoded username passes alone and fails in a full run.
- Both configs set `globally_quoted_identifiers=true` because `pov.user` and `canva.text` are reserved words in H2, plus `globally_quoted_identifiers_skip_column_definitions=true` so the quoting does not turn `columnDefinition="longtext"` into an unknown type (`TEST-04`).
- LIC entities must also be listed in the *test* `hibernate.cfg.xml` — it has its own `<mapping>` list, separate from production.
- All 14 Toolkit controller tests use Spring `MockMvc`. Note `applicationContext-test.xml` component-scans only `repository` and `service` — controllers are wired by the test itself, not by the context.
- Filters and servlets are tested with Mockito rather than a servlet container.

## Frontend

JSP + JSTL + Materialize, with jQuery. No build step, no bundler, no npm — scripts are committed and loaded with `<script src>`.

- **The pinned old libraries constrain how you write code.** jsPDF is from 2014, so calls have to probe `pdf.internal.pageSize.getWidth ? ... : pdf.internal.pageSize.width` (`CAN-16`) — the modern API is not there. Konva is on 10.3.0, so comments referring to 1.6.5 behaviour describe the older library, not the current one. Anything you change in an export path is exercised only by the client-side PDF pipeline, which has no automated coverage. See the version tables above.
- **Escaping is the application's job, not the framework's.** Materialize is pinned to a 0.9x release with no upstream successor, so nothing in the UI layer sanitizes for you — every output path has to escape explicitly, server-side or client-side.
- **`escapeHtml()` is copy-pasted per page.** Every JSP that injects server data into `innerHTML` defines its own local copy (currently `colaboracao.jsp`, `gerenciamento-ideia.jsp`, `lista-caixa-de-ferramentas.jsp`, `lista-canvas.jsp`, `minha-ideia.jsp`, `validar-ideia.jsp`). Server-side output uses JSTL `<c:out>`. A new page that renders data client-side needs its own escape helper — there is no shared one, and forgetting it re-opens stored XSS of the kind that was already swept out of the app once (`RELATORIO-FINAL.md` counts ~45 screens).
- **Cache-busting is manual**: `headerCookies_2.jsp` / `headerCookiesCV.jsp` append a `BUILD_TS` computed once per class load to the `<script>` URL. Pick the header variant that busts the JS your page actually needs.
- Storytelling autosaves the whole element array as JSON to `AutoSalvarStoryServlet`, which batches it into one SELECT plus one save (`PERF-02`) and refuses to write once the storytelling is finalized.
- **The app is not offline-safe, and jQuery loading is inconsistent.** `headerCookies.jsp`, `headerCookiesCV.jsp` and `headerCookiesColaboracao.jsp` pull jQuery from `code.jquery.com`; only `headerCookies_2.jsp` (Storytelling) uses the committed copy. The nine Canva pages end up loading jQuery **twice** — the remote one from `headerCookiesCV.jsp` plus a local `js/jquery-3.2.1.min.js` of the same version in the page itself. Google Fonts (84 references), Font Awesome from `maxcdn.bootstrapcdn.com` and a CSS reset from `cdnjs.cloudflare.com` are remote too, while Roboto exists locally under `webapp/fonts/roboto`. Offline, Storytelling and Canva still find a local jQuery; the rest of LIC loses it entirely, and polling, post-its and export flows all depend on it. Point the remote `<script>` tags at the committed copy if the deployment has to run air-gapped.

## Cross-cutting details

**There is no logging framework.** No slf4j, no log4j — diagnostics are ~10 `System.out`/`System.err` calls total across both projects, landing in `catalina.out` (`docker logs`). Notable ones are tagged (`LOG-SENDGRID` logs SendGrid's `X-Message-Id` on success and the rejection body on failure; `TK-EXC` logs unhandled Toolkit exceptions). Don't introduce a logging dependency for a one-off; do keep the tagged format if you add to an existing flow.

**Dates are formatted in `America/Sao_Paulo`, always.** `Data` pins the timezone on every `SimpleDateFormat` it hands out (`INFRA-TZ`) so output does not depend on the JVM's timezone; Docker also sets `TZ` on both build and runtime. `Data.formatarData(null)` returns an empty string rather than today's date (`INFRA-09`) — don't "fix" that into a default. Use `Data`'s helpers instead of formatting dates inline, or you lose the pinning.

**Session and cookie hardening lives in three files.** `web.xml` sets `http-only` and `tracking-mode COOKIE`; `META-INF/context.xml` sets `sameSiteCookies="lax"` on the session cookie in *both* projects (`SEC-02`), which is the CSRF mitigation that required no form changes; `SecurityHeadersFilter` adds `Referrer-Policy: strict-origin-when-cross-origin` on top of Tomcat's `HttpHeaderSecurityFilter`. The `secure` flag is deliberately **off** because dev runs over plain HTTP — turning it on is part of putting the app behind TLS, along with unblocking the Storytelling microphone, which browsers gate behind a secure context. No `session-timeout` is declared, so the container default applies.

## Checklists

Constraints in this file are scattered by topic; these are the ones that actually bite, gathered per task.

**Adding a LIC servlet**
1. Register it in `web.xml` (`<servlet>` + `<servlet-mapping>`) — there is no annotation scanning, no router.
2. Re-check `session.getAttribute("codigoUsuario")` and redirect to `login.jsp` when absent.
3. Check the user actually participates in the idea being touched (`SRV-IDOR-*` pattern), not just that they are logged in.
4. Refuse to write when the idea has left this module's status, and fail loudly (flash `mensagemErroEtapa` + redirect).
5. If it writes, add its path to `PostOnlyFilter`'s allowlist and give it a `doGet` that redirects instead of acting.
6. If it returns JSON, use `JsonUtil.GSON_SEM_SENHA`.
7. Read `ideiaId`/`storytellingId` from the session, not from a request parameter.

**Adding a LIC JSP**
1. Include one of `header/headerCookies*.jsp` — that include *is* the authentication check. Pick the variant whose script bundle you need.
2. Output server data with `<c:out>`; if you inject into `innerHTML`, define a local `escapeHtml()` in the page.
3. Any POST form needs the `csrfToken` field (or the `X-CSRF-Token` header for fetch/AJAX).
4. On Canva/Colaboração pages, wrap new editing controls in `<c:if test="${isRetencao eq false}">`.

**Adding a Toolkit controller or query**
1. Read the idea from `AdminCookies.getCookieIdeiaCodigo(request)` — it returns `null` when the HMAC does not validate; handle that (`return "redirect"`).
2. Gate writes with `StatusGuard.podeEscrever(...)`, and on failure use the flash + `redirect:/aviso-etapa-encerrada` hop.
3. **Every** query must filter by `ideiaCodigo` explicitly — the entities have no JPA relationship to `Ideia` to fall back on.
4. Views live in `/WEB-INF/view/*.jsp` and compose the page with the `WEB-INF/tags` custom tags (`<t:header>` / `<t:footer>`, plus `no-container-*` variants); `error.jsp` and `redirect.jsp` are the shared failure views.
5. If it touches the Empathy Map question flow, remember the same change has to be made in all six question controllers.

**Changing the schema**
1. Edit `db/init/estrutura-lic_bd.sql`, *and* add the manual `ALTER TABLE` note beside it — existing databases never pick the change up on their own.
2. Update the entity on **both** sides if the table is shared (`Ideia`, `Usuario`, `export_file`).
3. Add the entity to `hibernate.cfg.xml` **and** to the test `hibernate.cfg.xml` if it is new in LIC.

## Change log convention

Source comments carry a bracket key (`SEC-22`, `TK-35`, `SRV-IDOR-07`, `DAO-FAIL-CLOSED`, `UX-PADRAO-ETAPA-FINALIZADA`, …). The same key indexes a full entry in **`RELATORIO-CORRECOES.txt`** with root cause, files touched and how to revert. When you see an odd-looking construct with a tag, grep the tag in that file before changing it — the shape is usually deliberate. `RELATORIO-FINAL.md` is the executive summary plus the current open items.

## Configuration and secrets

- Secrets (`SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL`, `FEEDBACK_EMAIL`, `CAIXA_HMAC_SECRET`) are read with `System.getenv()`, never from XML — they must be real environment variables on the Tomcat process. There is no properties file and no config framework.
- `AES_KEY` appears in `.env.example` and `docker-compose.yml` but no code reads it — it is inert, not a hook to build on.
- **`app/entrypoint.sh` rewrites `hibernate.cfg.xml` and `toolkit-servlet.xml` with `sed` at container start**, injecting DB host/name/password, `hbm2ddl` and `show_sql`. The values committed in those files (`127.0.0.1:3306`, `root`/`root`, `hbm2ddl=update`, `show_sql=true`) are the local-dev defaults. Those `sed` expressions match on exact line shapes, so reformatting or re-quoting them and the patterns has to happen together.
- The Toolkit's Persona/POV repositories use MySQL-syntax **native queries**, which pins that side to MySQL and is what the H2 test config works around with quoted identifiers.
- `RELATORIO-FINAL.md` §3 records which security characteristics are deliberate, evaluated decisions rather than oversights — read it before changing anything in that area, so a considered trade-off does not get silently reversed.
