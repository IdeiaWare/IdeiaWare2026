package edu.unisc.lic.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// GT-01: JsonUtil.GSON_SEM_SENHA nunca inclui Usuario.senha, mesmo aninhado (ex.: ColaboracaoIdeia.usuario).
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
		// GT-01: caso real do vazamento (ColaboracaoIdeia.usuario serializado).
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
		// GT-01: confirma a premissa do bug -- sem o Gson customizado, o campo vazava.
		Usuario u = new Usuario("Nome", "login3", "hashQueVazava", "usr", "e3@x.com");
		u.setSenha("hashQueVazava", false);

		String json = new com.google.gson.Gson().toJson(u);

		assertTrue("documenta o comportamento padrao do Gson (motivo do bug)", json.contains("hashQueVazava"));
	}
}
