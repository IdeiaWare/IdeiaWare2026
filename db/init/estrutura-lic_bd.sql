-- =====================================================================
-- estrutura-lic_bd.sql  —  schema COMPLETO (LIC + Toolkit)
-- =====================================================================
-- Roda tudo de uma vez. No Ubuntu:
--     sudo mysql < estrutura-lic_bd.sql
-- (ou, se o root ja tem senha:  mysql -u root -p < estrutura-lic_bd.sql)
--
-- Cria: database lic_bd (utf8mb4) + 14 tabelas + usuario do app (root/root
-- via TCP 127.0.0.1) + 1 admin inicial pra logar (admin / admin).
--
-- Arquivo em UTF-8 (sem BOM). Idempotente: pode rodar de novo sem apagar dados
-- (usa CREATE ... IF NOT EXISTS).
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `lic_bd`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `lic_bd`;

-- ---------------------------------------------------------------------
-- 1) usuario  (raiz: ninguem depende de tabela anterior)
-- ---------------------------------------------------------------------
-- RACE-01: UNIQUE em usuario/email -- sem isso, 2 cadastros simultaneos com o mesmo login
-- passavam os 2 pela checagem em Java e inseriam duplicata (LogInServlet trava com >1 resultado).
CREATE TABLE IF NOT EXISTS `usuario` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permissao` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DataAnonimizado` datetime DEFAULT NULL,
  `anonimizado` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT 'N',
  `resetTokenHash` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resetTokenExpira` datetime DEFAULT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `uk_usuario_login` (`usuario`),
  UNIQUE KEY `uk_usuario_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Banco ja existente (deploy anterior a 2026-07-03) NAO ganha a constraint sozinho --
-- este script so roda no 1o boot de um volume vazio. Rodar manualmente uma vez:
--   ALTER TABLE usuario ADD UNIQUE KEY uk_usuario_login (usuario);
--   ALTER TABLE usuario ADD UNIQUE KEY uk_usuario_email (email);
-- (falha com "Duplicate entry" se ja existir duplicata real no banco -- resolver a
-- duplicata primeiro, ela ja seria o proprio bug RACE-01 acontecendo na pratica.)
--
-- RESET-TOKEN (2026-07-14): banco ja existente tambem precisa das 2 colunas novas:
--   ALTER TABLE usuario ADD COLUMN resetTokenHash varchar(64) DEFAULT NULL,
--     ADD COLUMN resetTokenExpira datetime DEFAULT NULL;

-- ---------------------------------------------------------------------
-- 2) ideia  (FK -> usuario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ideia` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(1500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dtCriacao` datetime NOT NULL,
  `dtFimDesenv` datetime DEFAULT NULL,
  `dtInicioDesenv` datetime DEFAULT NULL,
  `dtRejeicao` datetime DEFAULT NULL,
  `dtValidacao` datetime DEFAULT NULL,
  `motivoRejeicao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `statusGrupo` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `titulo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gestor_codigo` bigint(20) DEFAULT NULL,
  `usuario_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FKr9gbp0louu7pph9n2jicoqag7` (`gestor_codigo`),
  KEY `FKjhv12shnbpw2b6nuok1sjrjht` (`usuario_codigo`),
  KEY `idx_ideia_status` (`status`),
  CONSTRAINT `FKjhv12shnbpw2b6nuok1sjrjht` FOREIGN KEY (`usuario_codigo`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `FKr9gbp0louu7pph9n2jicoqag7` FOREIGN KEY (`gestor_codigo`) REFERENCES `usuario` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3) persona  (Toolkit, FK -> ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `persona` (
  `persona_id` int(11) NOT NULL AUTO_INCREMENT,
  `ideia_codigo` bigint(20) NOT NULL,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`persona_id`),
  KEY `ideia_codigo` (`ideia_codigo`),
  CONSTRAINT `persona_ibfk_1` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4) pov  (Toolkit, FK -> ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pov` (
  `ideia_codigo` bigint(20) NOT NULL,
  `pov_id` int(11) NOT NULL AUTO_INCREMENT,
  `need` longtext COLLATE utf8mb4_unicode_ci,
  `insight` longtext COLLATE utf8mb4_unicode_ci,
  `user` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`pov_id`),
  KEY `ideia_codigo` (`ideia_codigo`),
  CONSTRAINT `pov_ibfk_1` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5) persona_pov  (Toolkit, FK -> persona, pov, ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `persona_pov` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `persona_id` int(11) NOT NULL,
  `pov_id` int(11) NOT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `persona_id` (`persona_id`),
  KEY `pov_id` (`pov_id`),
  KEY `ideia_codigo` (`ideia_codigo`),
  -- sem isso, duplo-clique em "Salvar" no form de POV duplicava a linha persona<->POV.
  UNIQUE KEY `uk_persona_pov_persona_pov` (`persona_id`,`pov_id`),
  CONSTRAINT `persona_pov_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`persona_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `persona_pov_ibfk_2` FOREIGN KEY (`pov_id`) REFERENCES `pov` (`pov_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `persona_pov_ibfk_3` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 6) empathy  (Toolkit, FK -> persona, ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `empathy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ideia_codigo` bigint(20) NOT NULL,
  `fk_persona_id` int(11) NOT NULL,
  `attribute_text` longtext COLLATE utf8mb4_unicode_ci,
  `card_color` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attribute` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_persona_id` (`fk_persona_id`),
  KEY `ideia_codigo` (`ideia_codigo`),
  KEY `idx_empathy_lookup` (`fk_persona_id`,`attribute`,`ideia_codigo`),
  CONSTRAINT `empathy_ibfk_1` FOREIGN KEY (`fk_persona_id`) REFERENCES `persona` (`persona_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `empathy_ibfk_2` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7) ideiausuario  (LIC, FK -> ideia, usuario)
-- ---------------------------------------------------------------------
-- K.8 #2: UNIQUE em (usuario_codigo, ideia_codigo) -- sem isso, clique duplo em "Entrar"
-- inseria o mesmo usuario 2x na mesma ideia (a KEY antiga so acelerava, nao travava).
CREATE TABLE IF NOT EXISTS `ideiausuario` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtInscricao` datetime DEFAULT NULL,
  `flLider` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  -- M.2/M.3 (2026-07-06): lista de espera do grupo. flStatusVinculo = P(endente)/
  -- A(provado)/R(ejeitado); motivoRejeicaoMembro = motivo quando o lider recusa a entrada.
  `flStatusVinculo` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `motivoRejeicaoMembro` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  `usuario_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FK5a3ec83gur52ox47jh3p2m5xa` (`ideia_codigo`),
  UNIQUE KEY `uk_ideiausuario_par` (`usuario_codigo`,`ideia_codigo`),
  CONSTRAINT `FK5a3ec83gur52ox47jh3p2m5xa` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`),
  CONSTRAINT `FK6wjmsj6giwc74vyr9pgk6cr8a` FOREIGN KEY (`usuario_codigo`) REFERENCES `usuario` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Banco ja existente (deploy anterior a 2026-07-06) NAO ganha a constraint sozinho --
-- rodar manualmente uma vez: ALTER TABLE ideiausuario ADD UNIQUE KEY uk_ideiausuario_par
-- (usuario_codigo, ideia_codigo); (falha com "Duplicate entry" se ja existir duplicata
-- real -- resolver a duplicata primeiro, ex.: manter a linha mais antiga e apagar as demais.)
--
-- M.2/M.3 (2026-07-06): banco ja existente tambem precisa das colunas novas + marcar os
-- vinculos ATUAIS como aprovados (eram membros efetivos antes da lista de espera existir):
--   ALTER TABLE ideiausuario ADD COLUMN flStatusVinculo varchar(1) DEFAULT NULL,
--     ADD COLUMN motivoRejeicaoMembro varchar(200) DEFAULT NULL;
--   UPDATE ideiausuario SET flStatusVinculo = 'A' WHERE flStatusVinculo IS NULL;
-- (No codigo, flStatusVinculo NULL ja e tratado como aprovado, entao o UPDATE e opcional,
--  so deixa os dados explicitos.)

-- ---------------------------------------------------------------------
-- 8) logcolaboracao  (LIC, FK -> ideia, usuario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `logcolaboracao` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(1500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dtModificacao` datetime DEFAULT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  `usuario_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FKsrwwx5d3yglxaojv8ilsf46ye` (`ideia_codigo`),
  KEY `FKg5jr87nbyt614un2s29i1y1t5` (`usuario_codigo`),
  CONSTRAINT `FKg5jr87nbyt614un2s29i1y1t5` FOREIGN KEY (`usuario_codigo`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `FKsrwwx5d3yglxaojv8ilsf46ye` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 9) colaboracaoideia  (LIC, FK -> ideia, usuario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `colaboracaoideia` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricaoIdeiaAnterior` varchar(1500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descricaoIdeiaAtual` varchar(1500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descricaoIdeiaLider` varchar(1500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dtModificacao` datetime DEFAULT NULL,
  `flEditadoLider` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `flSalvado` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  `usuario_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FKfoi1x0yumecgmls5lvw4so4fg` (`ideia_codigo`),
  KEY `FK8l81aud7ivvst0pxjed6in46c` (`usuario_codigo`),
  CONSTRAINT `FK8l81aud7ivvst0pxjed6in46c` FOREIGN KEY (`usuario_codigo`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `FKfoi1x0yumecgmls5lvw4so4fg` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 10) storytelling  (LIC, FK -> ideia, usuario)
-- ---------------------------------------------------------------------
-- K.8 #3: UNIQUE em ideia_codigo -- 1 storytelling por ideia e regra de negocio (@OneToOne),
-- mas sem essa trava, "Finalizar Colaboracao" 2x quase-simultaneo duplicava a linha.
CREATE TABLE IF NOT EXISTS `storytelling` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `caminhoFinalizado` varchar(255) COLLATE utf8mb4_unicode_ci,
  `dtCriacao` datetime DEFAULT NULL,
  `dtFinalizacao` datetime DEFAULT NULL,
  `status` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  `usuario_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `uk_storytelling_ideia` (`ideia_codigo`),
  KEY `FK4hb323iwc39gt5yv0e135juoa` (`usuario_codigo`),
  CONSTRAINT `FK4hb323iwc39gt5yv0e135juoa` FOREIGN KEY (`usuario_codigo`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `FKq2tqmjwc02je1hyquqj5a0sla` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Banco ja existente (deploy anterior a 2026-07-06): ALTER TABLE storytelling ADD UNIQUE
-- KEY uk_storytelling_ideia (ideia_codigo); (falha se ja existir duplicata real -- limpar
-- antes, mantendo a linha mais recente/valida.)

-- ---------------------------------------------------------------------
-- 11) elementosstorytelling  (LIC, FK -> storytelling)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `elementosstorytelling` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `altura` double DEFAULT NULL,
  `camada` int(11) DEFAULT NULL,
  `caminho` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `corFonte` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fonte` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `informacaoTexto` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `largura` double DEFAULT NULL,
  `tamanhoFonte` int(11) DEFAULT NULL,
  `tipo` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipoFonte` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `storytelling_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FKjfnyqlv4uw2xlnmb9trnlc13` (`storytelling_codigo`),
  CONSTRAINT `FKjfnyqlv4uw2xlnmb9trnlc13` FOREIGN KEY (`storytelling_codigo`) REFERENCES `storytelling` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 12) canva  (LIC, FK -> ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `canva` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `text` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `FKc8m0i5tbqloxghlh06ccnldam` (`ideia_codigo`),
  CONSTRAINT `FKc8m0i5tbqloxghlh06ccnldam` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 13) canvaexport  (LIC, FK -> ideia)
-- ---------------------------------------------------------------------
-- K.8 #4: UNIQUE em ideia_codigo -- 1 export de Canvas por ideia (ExportCanvaServlet
-- atualiza se ja existe), mas sem essa trava, 2 exports quase-simultaneos duplicavam a linha.
CREATE TABLE IF NOT EXISTS `canvaexport` (
  `codigo` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `file` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `uk_canvaexport_ideia` (`ideia_codigo`),
  CONSTRAINT `FKc7v6vqa68cgd1syiblxxq2n52` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Banco ja existente (deploy anterior a 2026-07-06): ALTER TABLE canvaexport ADD UNIQUE
-- KEY uk_canvaexport_ideia (ideia_codigo); (falha se ja existir duplicata real -- limpar
-- antes, mantendo a linha mais recente.)

-- ---------------------------------------------------------------------
-- 14) export_file  (compartilhada LIC/Toolkit, FK -> ideia)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `export_file` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_location` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_type_identification` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ideia_codigo` bigint(20) NOT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `FKkmu2h351lu3wuotvbgjqcuid0` (`ideia_codigo`),
  KEY `idx_exportfile_tipo` (`file_type_identification`),
  CONSTRAINT `FKkmu2h351lu3wuotvbgjqcuid0` FOREIGN KEY (`ideia_codigo`) REFERENCES `ideia` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- USUARIO DO BANCO QUE O APP USA  (root/root via TCP 127.0.0.1)
-- ---------------------------------------------------------------------
-- O LIC e o Toolkit conectam em jdbc:mysql://127.0.0.1:3306 com root/root.
-- No MySQL, root@'localhost' (default) so vale para SOCKET; conexao TCP em
-- 127.0.0.1 precisa de um usuario root@'127.0.0.1'. Por isso criamos ele aqui
-- com mysql_native_password (o driver 5.1 do projeto NAO fala caching_sha2 do
-- MySQL 8). NAO mexemos no root@'localhost' -> seu 'sudo mysql' continua igual.
--
-- >>> Sintaxe abaixo = MySQL 8 / 5.7. Se voce usar MARIADB, troque as 2 linhas
--     CREATE/ALTER por:
--       CREATE OR REPLACE USER 'root'@'127.0.0.1' IDENTIFIED BY 'root';
-- =====================================================================
CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'root';
ALTER USER 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'root';
GRANT ALL PRIVILEGES ON `lic_bd`.* TO 'root'@'127.0.0.1';
FLUSH PRIVILEGES;

-- =====================================================================
-- ADMIN INICIAL  (pra voce ja conseguir logar no LIC)
-- ---------------------------------------------------------------------
--   usuario: admin     senha: admin
-- A senha esta em BCRYPT (o app usa bcrypt desde o SEC-22). Este hash valida 'admin'.
-- TROQUE a senha depois pelo proprio sistema (perfil) ou remova este usuario
-- em producao. So insere se ainda nao existir um 'admin'.
-- =====================================================================
INSERT INTO `usuario` (`nome`, `permissao`, `senha`, `usuario`, `email`, `anonimizado`)
SELECT 'Administrador', 'adm',
       '$2a$10$DKVPQ2VEyDEoH/I8koxS1..kulsiVpc5CbGKCDbu1WoNqhorkcZwi',
       'admin', 'admin@local', 'N'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM (SELECT `usuario` FROM `usuario` WHERE `usuario` = 'admin') AS _ja);

-- =====================================================================
-- Fim. Confira:  USE lic_bd; SHOW TABLES;  (devem aparecer 14 tabelas)
-- =====================================================================
