package edu.unisc.lic.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// TEST-04: logica pura de Usuario -- hash bcrypt (SEC-22) e anonimizacao (LGPD), nenhum tinha teste unitario direto.
public class UsuarioTest {

	@Test
	public void construtorUsuarioSenha_armazenaHashBcryptNaoTextoPuro() {
		Usuario u = new Usuario("joao123", "minhaSenha!");

		assertNotEquals("minhaSenha!", u.getSenha());
		assertTrue("hash bcrypt comeca com $2a$/$2b$/$2y$", u.getSenha().startsWith("$2"));
	}

	@Test
	public void checaSenha_senhaCorreta_retornaTrue() {
		Usuario u = new Usuario("joao123", "minhaSenha!");
		assertTrue(u.checaSenha("minhaSenha!"));
	}

	@Test
	public void checaSenha_senhaErrada_retornaFalse() {
		Usuario u = new Usuario("joao123", "minhaSenha!");
		assertFalse(u.checaSenha("senhaErrada"));
	}

	@Test
	public void checaSenha_hashInvalidoOuAusente_naoLancaExcecaoRetornaFalse() {
		Usuario u = new Usuario();
		u.setSenha("hash-legado-corrompido-nao-e-bcrypt");
		assertFalse(u.checaSenha("qualquerSenha"));

		Usuario semSenha = new Usuario();
		assertFalse(semSenha.checaSenha("qualquerSenha"));
	}

	@Test
	public void checaSenha_senhaEmClaroNula_retornaFalse() {
		Usuario u = new Usuario("joao123", "minhaSenha!");
		assertFalse(u.checaSenha(null));
	}

	@Test
	public void setSenhaComCriptografiaFalse_gravaEmClaroSemHashear() {
		Usuario u = new Usuario();
		u.setSenha("textoPuro", false);
		assertEquals("textoPuro", u.getSenha());
	}

	@Test
	public void setSenhaComCriptografiaTrue_hasheiaComBcrypt() {
		Usuario u = new Usuario();
		u.setSenha("textoPuro", true);
		assertNotEquals("textoPuro", u.getSenha());
		assertTrue(u.checaSenha("textoPuro"));
	}

	@Test
	public void resetaSenha_geraSenhaDe10CaracteresEJaHasheada() {
		Usuario u = new Usuario("joao123", "senhaAntiga");
		String senhaNova = u.ResetaSenha();

		assertEquals(10, senhaNova.length());
		assertTrue("a senha resetada deve autenticar contra o novo hash", u.checaSenha(senhaNova));
		assertFalse("a senha antiga nao deve autenticar mais", u.checaSenha("senhaAntiga"));
	}

	@Test
	public void anonimizaDadosPessoais_substituiCamposEMarcaComoAnonimizado() {
		Usuario u = new Usuario("Joao da Silva", "joao123", "senha", "col", "joao@email.com");
		u.setCodigo(42L);
		String senhaAntesDoHash = u.getSenha();

		u.AnonimizaDadosPessoais();

		assertEquals("Nome anonimizado 42", u.getNome());
		assertEquals("UsuarioAnonimizado42", u.getUsuario());
		assertEquals("EmailAnonimizado42@anonimizado.com", u.getEmail());
		assertEquals("S", u.getAnonimizado());
		assertNotNull("data de anonimizacao deve ser preenchida", u.getDataAnonimizado());
		assertNotEquals("a senha deve ser resetada (ResetaSenha)", senhaAntesDoHash, u.getSenha());
	}

	@Test
	public void getDadosPessoais_formataComDelimitadorPersonalizado() {
		Usuario u = new Usuario("Joao", "joao123", "senha", "col", "joao@email.com");
		String dados = u.getDadosPessoais(" | ");

		assertTrue(dados.contains("Usuário: joao123"));
		assertTrue(dados.contains("Nome: Joao"));
		assertTrue(dados.contains("E-mail: joao@email.com"));
	}
}
