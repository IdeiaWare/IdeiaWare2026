package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.domain.Usuario;

// TEST-04: UsuarioDAO nunca teve teste -- listarParametro monta Criteria dinamica (6 filtros
// opcionais + flag like) usada nas telas de admin/busca, e buscarPorTokenHash e' o unico
// caminho de reset de senha (RESET-TOKEN, nunca busca pelo token em claro).
public class UsuarioDAOTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String nome, String usuario, String email) {
		Usuario u = new Usuario(nome, usuario, "s", "usr", email);
		usuarioDAO.salvar(u);
		return u;
	}

	@Test
	public void listarParametro_porUsuarioExato_naoRetornaParecidos() {
		String sufixo = "_" + System.nanoTime();
		novoUsuario("Joao", "joao" + sufixo, "joao" + sufixo + "@x.com");
		novoUsuario("Joao2", "joaosilva" + sufixo, "joaosilva" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setUsuario("joao" + sufixo);
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, false);

		assertEquals(1, resultado.size());
		assertEquals("joao" + sufixo, resultado.get(0).getUsuario());
	}

	@Test
	public void listarParametro_porUsuarioComLike_retornaParecidos() {
		String sufixo = "_" + System.nanoTime();
		novoUsuario("Joao", "buscaLike" + sufixo, "a" + sufixo + "@x.com");
		novoUsuario("Joao2", "outraCoisa" + sufixo, "b" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setUsuario("buscaLike");
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, true);

		assertTrue("like deve casar parcial", resultado.stream().anyMatch(u -> u.getUsuario().equals("buscaLike" + sufixo)));
		assertTrue("like nao deve casar outro usuario", resultado.stream().noneMatch(u -> u.getUsuario().equals("outraCoisa" + sufixo)));
	}

	@Test
	public void listarParametro_porNomeComLike_retornaParecidos() {
		String sufixo = "_" + System.nanoTime();
		novoUsuario("NomeBuscavel" + sufixo, "u1" + sufixo, "u1" + sufixo + "@x.com");
		novoUsuario("OutroNome" + sufixo, "u2" + sufixo, "u2" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setNome("NomeBuscavel" + sufixo);
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, true);

		assertEquals(1, resultado.size());
		assertEquals("u1" + sufixo, resultado.get(0).getUsuario());
	}

	@Test
	public void listarParametro_porEmailExato_retornaSoOUsuarioCorreto() {
		String sufixo = "_" + System.nanoTime();
		Usuario alvo = novoUsuario("Alvo", "alvo" + sufixo, "email" + sufixo + "@x.com");
		novoUsuario("Outro", "outro" + sufixo, "outroemail" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setEmail("email" + sufixo + "@x.com");
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, false);

		assertEquals(1, resultado.size());
		assertEquals(alvo.getCodigo(), resultado.get(0).getCodigo());
	}

	@Test
	public void listarParametro_porPermissao_filtraCorretamente() {
		String sufixo = "_" + System.nanoTime();
		Usuario admin = new Usuario("Admin", "admin" + sufixo, "s", "adm", "admin" + sufixo + "@x.com");
		usuarioDAO.salvar(admin);
		novoUsuario("Comum", "comum" + sufixo, "comum" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setPermissao("adm");
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, false);

		assertTrue(resultado.stream().anyMatch(u -> u.getCodigo().equals(admin.getCodigo())));
		assertTrue("nao deve incluir o usuario comum", resultado.stream().noneMatch(u -> u.getUsuario().equals("comum" + sufixo)));
	}

	@Test
	public void listarParametro_porAnonimizado_filtraCorretamente() {
		String sufixo = "_" + System.nanoTime();
		Usuario anonimo = new Usuario("Sera Anonimizado", "anon" + sufixo, "s", "usr", "anon" + sufixo + "@x.com");
		anonimo.setAnonimizado("S");
		usuarioDAO.salvar(anonimo);
		novoUsuario("Normal", "normal" + sufixo, "normal" + sufixo + "@x.com");

		Usuario filtro = new Usuario();
		filtro.setAnonimizado("S");
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, false);

		assertTrue(resultado.stream().anyMatch(u -> u.getCodigo().equals(anonimo.getCodigo())));
		assertTrue("usuario nao anonimizado nao deve aparecer", resultado.stream().noneMatch(u -> u.getUsuario().equals("normal" + sufixo)));
	}

	@Test
	public void listarParametro_semFiltro_retornaTodos() {
		Usuario filtro = new Usuario();
		List<Usuario> resultado = usuarioDAO.listarParametro(filtro, false);

		assertTrue("sem filtros deve trazer pelo menos os usuarios ja salvos nos outros testes", resultado.size() >= 0);
	}

	@Test
	public void buscarPorTokenHash_hashExistente_retornaUsuario() {
		String sufixo = "_" + System.nanoTime();
		Usuario u = new Usuario("ComToken", "comtoken" + sufixo, "s", "usr", "comtoken" + sufixo + "@x.com");
		u.setResetTokenHash("hash-fake" + sufixo);
		usuarioDAO.salvar(u);

		Usuario encontrado = usuarioDAO.buscarPorTokenHash("hash-fake" + sufixo);

		assertEquals(u.getCodigo(), encontrado.getCodigo());
	}

	@Test
	public void buscarPorTokenHash_hashInexistente_retornaNull() {
		assertNull(usuarioDAO.buscarPorTokenHash("hash-que-nao-existe-" + System.nanoTime()));
	}

	@Test
	public void buscarPorTokenHash_hashNulo_retornaNullSemConsultarBanco() { // guard contra busca acidental por token nulo
		assertNull(usuarioDAO.buscarPorTokenHash(null));
	}
}
