package edu.unisc.lic.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * REVISAO 2026-07-07 (varredura de servlets): JsonUtil.GSON_SEM_SENHA nunca deve incluir
 * Usuario.senha (hash bcrypt) na saida JSON, mesmo quando o Usuario aparece aninhado dentro
 * de outra entidade (ex.: ColaboracaoIdeia.usuario, o caso real do vazamento encontrado).
 */
public class JsonUtilTest {

	@Test
	public void gsonSemSenha_naoIncluiCampoSenhaDeUsuarioDireto() {
		Usuario u = new Usuario("Nome", "login", "hashBcryptSecreto", "usr", "e@x.com");
		u.setSenha("hashBcryptSecreto", false);

		String json = JsonUtil.GSON_SEM_SENHA.toJson(u);

		assertFalse("campo senha nao deve aparecer no JSON", json.contains("senha"));
		assertFalse("hash nao deve vazar", json.contains("hashBcryptSecreto"));
		assertTrue("outros campos continuam presentes", json.contains("Nome"));
	}

	@Test
	public void gsonSemSenha_naoIncluiSenhaDeUsuarioAninhadoEmColaboracaoIdeia() {
		// Caso real do vazamento: ColaboracaoIdeia.usuario serializado via
		// EnviarColaboracaoServlet/RetornaMensagensServlet/EditarColaboracaoServlet.
		Usuario u = new Usuario("Colaborador", "login2", "x", "usr", "e2@x.com");
		u.setSenha("hashBcryptDoColaborador", false);
		Ideia ideia = new Ideia(u, "Titulo", "Descricao", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ColaboracaoIdeia colab = new ColaboracaoIdeia(ideia, u, null, "texto da colaboracao");

		String json = JsonUtil.GSON_SEM_SENHA.toJson(colab);

		assertFalse("hash do autor da colaboracao nao deve vazar", json.contains("hashBcryptDoColaborador"));
		assertTrue("nome do colaborador continua presente", json.contains("Colaborador"));
		assertTrue("texto da colaboracao continua presente", json.contains("texto da colaboracao"));
	}

	@Test
	public void gsonPadrao_paraComparacao_incluiriaSenha() {
		// Confirma a premissa do bug: sem o Gson customizado, o campo vazava mesmo.
		Usuario u = new Usuario("Nome", "login3", "hashQueVazava", "usr", "e3@x.com");
		u.setSenha("hashQueVazava", false);

		String json = new com.google.gson.Gson().toJson(u);

		assertTrue("documenta o comportamento padrao do Gson (motivo do bug)", json.contains("hashQueVazava"));
	}
}
