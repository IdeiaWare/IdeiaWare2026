# Relatório Final — IdeiaWare (LIC) + Toolkit2025

**Data:** 2026-07-21
**Período coberto:** revisão contínua desde 2026-06-08, com a maior parte do trabalho concentrada entre 2026-07-13 e 2026-07-21.

Este documento é um resumo executivo, pensado pra quem for continuar o projeto (ou decidir
não continuar) entender rápido o que foi feito e o que ainda está em aberto. Detalhe técnico
completo de cada item (causa raiz, arquivos, como reverter) está em `RELATORIO-CORRECOES.txt`
— cada item aqui tem uma chave `[TAG]` que aparece também como comentário no código-fonte.

---

## 1. Números gerais

- **~394 itens** de correção/melhoria catalogados no `RELATORIO-CORRECOES.txt`.
- **LIC: 368 testes automatizados** (partiu de 0). **Toolkit: 203 testes** (partiu de 0).
- Cobertura de código (JaCoCo): LIC ~85% de instruções, Toolkit ~55-57%.
- Docker completo (multi-stage, roda os testes durante o build — se um teste falhar, a
  imagem não é gerada).
- Autenticação migrada de SHA-256 pra bcrypt; CSRF implementado nos 2 projetos (não existia);
  headers de segurança adicionados; ~45 telas com XSS corrigido.

---

## 2. O que foi feito (resumo por categoria)

### Segurança
- Senha: SHA-256 → bcrypt.
- CSRF (double-submit cookie) implementado do zero nos 2 projetos.
- ~45 telas com XSS corrigido (incluindo casos sutis de re-injeção client-side, não só a
  saída direta em HTML).
- Vários IDOR fechados (~25 pontos onde um usuário conseguia acessar/editar dado de outra
  ideia só trocando um ID na URL/request).
- Cookie `ideiaId` (integração LIC↔Toolkit) passou a ser assinado (HMAC-SHA256) — antes era
  forjável via DevTools.
- Reset de senha: era imediato e inseguro (mandava a senha nova por e-mail); virou fluxo por
  link com token de uso único e expiração de 1h.
- Rate-limit em login/reset de senha/cadastro (não existia).
- Vazamento do hash da senha via JSON corrigido em vários endpoints (serialização
  ingênua trazia o campo `senha` junto).
- Endpoints destrutivos que aceitavam GET (vazamento de CSRF token por histórico/proxy)
  migrados pra POST.
- Upload de arquivo (Storytelling): faltava validação de tipo — corrigido (allowlist de
  extensão + nome aleatório + confirmação real de que é imagem).

### Bugs reais corrigidos (comportamento/dados errados, não só segurança)
- Botão "Enviar" do Canva ficava permanentemente travado se o usuário não clicasse numa cor
  do post-it antes — bug grave, afetava as 9 páginas do módulo.
- POV (Point of View) ficava órfão no banco ao apagar a única Persona vinculada.
- Reexportar PDF (Storytelling, depois Canva) sobrescrevia a exportação anterior em
  silêncio.
- "Finalizar Caixa de Ferramentas" duas vezes **regredia** o status da ideia em vez de
  travar.
- Módulos (Canva, Toolkit, Storytelling, Colaboração) não bloqueavam escrita depois que a
  etapa já tinha sido finalizada por outro colaborador/aba — corrigido de ponta a ponta,
  incluindo mensagem de aviso + redirecionamento consistente em todos os módulos.
- Diversos bugs de concorrência: 2 usuários simultâneos causando corrida em DAOs, sessão
  Hibernate guardada como campo de instância (bug real de concorrência em 10 DAOs), auto-save
  do Storytelling perdendo dados.
- PDFs eram salvos como base64 dentro do banco (LONGTEXT) — migrados pra disco.
- Vários bugs de exportação de PDF (Canva/Storytelling/Toolkit): paginação cortada, fundo
  transparente virando preto, erro "Packet too large" do MySQL.
- Editor de Storytelling (Konva): múltiplos bugs de arraste, resize e persistência de
  posição corrigidos após um upgrade de versão da biblioteca (1.6.5 → 10.3.0).

### UX / Consistência
- Padronização completa de mensagem + redirecionamento pro caso "essa etapa já foi
  encerrada" em todos os módulos (Canva, Toolkit, Storytelling, Colaboração).
- Botões de ação com tamanho inconsistente em "Minhas Ideias" — causa raiz era limitação de
  `<input type="submit">`, resolvido trocando por `<button>`.
- Cores por módulo padronizadas (cada módulo tem sua cor, ficou inconsistente por várias
  rodadas de ajuste de UI).
- Botões sem efeito hover (Materialize com `!important` travando a cor) corrigidos em 4
  páginas.
- Acessibilidade: `lang="pt-BR"`, `alt` em imagens, `aria-label` em vários pontos, `<main>`
  semântico nas páginas.
- Menu mobile do Toolkit (quebrado em todas as páginas) corrigido.
- Dezenas de ajustes pontuais de copy, espaçamento, contraste e capitalização (ex.: "Point
  of View" tinha 3 grafias diferentes pelo app).

### Performance
- Vários N+1 corrigidos (Storytelling, Colaboração).
- Queries duplicadas eliminadas.
- Escrita de arquivo (export) tornada atômica (escreve em `.tmp` + move) — evita arquivo
  corrompido se o processo morrer no meio.

### Qualidade / Infra
- Docker: setup completo do zero (multi-stage build, roda os testes, volumes persistentes
  pra PDFs/imagens).
- 571 testes automatizados escritos (LIC + Toolkit somados) — os 2 projetos não tinham
  nenhum teste antes.
- JaCoCo (cobertura de código) configurado nos 2 projetos.
- README com deploy manual (sem Docker) documentado do zero.
- Limpeza de código morto (~50MB de libs não usadas, comentários verbosos condensados pra
  chave + descrição breve).

---

## 3. Pendências

### 🔴 Ação necessária antes de um deploy real de produção
- **`CAIXA_HMAC_SECRET`**: tem um valor de fallback hardcoded no código-fonte (está no Git,
  é público). Se o deploy não setar essa variável de ambiente com um valor próprio, o
  cookie que assina a integração LIC↔Toolkit fica forjável. **Precisa ser setada.**
- **Chave do SendGrid**: a usada durante o desenvolvimento vazou no histórico do Git —
  precisa ser **revogada** e uma nova gerada, configurada só via variável de ambiente daqui
  pra frente.
- **E-mail de reset de senha não é entregue no Gmail** (rejeição hard 550 5.7.1,
  "likely unsolicited mail" — específico do remetente atual, `@outlook.com`). Confirmado que
  não é bug de código (SendGrid mostra envio aceito). Solução real exige **Domain
  Authentication** com um domínio próprio do projeto — trocar de provedor de e-mail sozinho
  não resolve, tem a mesma exigência.
- Sem HTTPS em produção, a gravação de áudio do Storytelling fica bloqueada (navegadores
  exigem contexto seguro pra usar o microfone) — sem solução via código, depende de expor o
  app com TLS.

### 🟠 Segurança conhecida, decisão consciente de não corrigir (documentada)
- `LogInServlet` não regenera o ID de sessão após autenticar (session fixation) — decisão
  explícita de não mexer.
- Canva e Storytelling não travam edição só pro líder do grupo no código — qualquer
  participante consegue editar (só o acesso à ideia é checado, não o papel).
- Materialize (framework CSS/JS usado nos 2 projetos) tem uma CVE de XSS
  (CVE-2022-25349) sem correção disponível upstream — mitigado só pela disciplina de escape
  já aplicada no app, não pela lib em si.
- CSP (Content Security Policy) não implementado — decisão deliberada (app roda localmente/
  uso interno, não é um SaaS exposto publicamente; o ganho real seria pequeno já que o app
  usa `unsafe-inline` em quase toda página).

### 🟡 Qualidade / arquitetura (sem urgência, sem pressa pra mexer)
- `AES_KEY` no `.env.example`/`docker-compose.yml` não é usada por nenhum código atual —
  candidata a remoção numa faxina futura.
- Padrão "session-per-DAO-method" no LIC (sem OpenSessionInView) — funciona, mas trocar
  exigiria mexer em todos os DAOs; avaliado e adiado por risco/custo.
- `GenericDAO` não tem um helper compartilhado pro guard "fail-closed"/checagem de dono —
  cada DAO/servlet novo copiado corre o risco de esquecer o padrão.
- `PostOnlyFilter` (LIC) é uma allowlist mantida manualmente — um servlet de escrita novo
  precisa lembrar de se cadastrar nela.
- Duas falhas de JS conhecidas no Storytelling (sem fix "mecânico" simples, dependem de
  decisão de produto): (1) 2 abas do mesmo usuário podem gerar 2 figuras sobrepostas por
  cálculo de posição local, não no servidor; (2) gravar áudio 2x antes de salvar só envia a
  última gravação (variável local sobrescrita).
- Migração pra Postgres foi avaliada (esforço médio, principal obstáculo é native query com
  sintaxe MySQL no Toolkit) — não decidida.

### 🔵 UX a revisitar (ideias prontas, não implementadas)
- Botão dedicado pra criar POV direto em `list-point-of-view.jsp` (hoje só existe via
  checkbox dentro do fluxo de Persona) — proposta pronta e aprovada, implementação adiada.
- Ícones de ação nas tabelas do Toolkit (editar/excluir/visualizar) são só ícone, sem texto
  visível — usuário decidiu manter assim por ora.
- Habilitar listas (bolinha/numerada) no editor de descrição (Quill) — depende de
  sanitização server-side do rich-text pra não abrir XSS armazenado; descartado por ora
  (ganho pequeno).
- Edição de uma colaboração já existente não aparece em tempo real pro resto do grupo (só
  colaborações **novas** aparecem via polling) — assimetria conhecida, não é prioridade.
- Um protótipo de tempo real (SSE) pra Colaboração foi implementado, testado ao vivo e
  **rejeitado** — revertido por completo. Se o assunto voltar, não reaproveitar sem
  reconfirmar que a decisão mudou.

### Ideias futuras (sem urgência, anotadas)
- Convite por e-mail pro grupo (link de convite) — depende da infra de e-mail estar redonda
  primeiro (ver pendência do Domain Authentication acima).

---

## 4. Onde encontrar mais detalhe

- **`RELATORIO-CORRECOES.txt`** — log completo, item por item, com causa raiz, arquivos
  tocados e como reverter cada mudança. Busque pela chave entre colchetes (ex.: `[TK-35]`,
  `[SEC-23]`) — a mesma chave aparece como comentário no código correspondente.
- **`README.md`** — como rodar o projeto (Docker ou manual), variáveis de ambiente
  explicadas uma a uma.
