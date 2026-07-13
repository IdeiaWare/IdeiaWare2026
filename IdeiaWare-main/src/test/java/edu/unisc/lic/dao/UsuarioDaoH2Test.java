package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.domain.Usuario;

// TEST-03: integracao real de UsuarioDAO contra H2 -- exercita o round-trip de persistencia com o GenericDAO/HibernateUtil de producao.
public class UsuarioDaoH2Test {

	private final UsuarioDAO dao = new UsuarioDAO();

	private Usuario novo(String nome) {
		// login unico por teste (a base H2 e compartilhada entre os testes do run)
		return new Usuario(nome, "login_" + System.nanoTime(), "senha123", "usr", nome + "@x.com");
	}

	@Test
	public void salvar_geraIdEPersisteOsCampos() {
		Usuario u = novo("Joao Silva");
		dao.salvar(u);

		assertNotNull("codigo deve ser gerado ao salvar", u.getCodigo());

		Usuario carregado = dao.buscar(u.getCodigo());
		assertNotNull("deve recarregar do banco", carregado);
		assertEquals("Joao Silva", carregado.getNome());
		assertEquals(u.getUsuario(), carregado.getUsuario());
		assertEquals(u.getEmail(), carregado.getEmail());
	}

	@Test
	public void buscar_idInexistente_retornaNull() {
		assertNotNull(dao); // sanity
		Usuario inexistente = dao.buscar(999999L);
		org.junit.Assert.assertNull(inexistente);
	}

	@Test
	public void listar_aumentaAoSalvar() {
		int antes = dao.listar().size();
		dao.salvar(novo("Maria"));
		assertEquals(antes + 1, dao.listar().size());
	}

	// TEST-04: reativado do scratch @Ignore original (UsuarioDAOTest.excluir/editar/listarParametro).
	@Test
	public void excluir_removeORegistro() {
		Usuario u = novo("Vai ser apagado");
		dao.salvar(u);
		Long codigo = u.getCodigo();

		dao.excluir(u);

		assertNull(dao.buscar(codigo));
	}

	@Test
	public void editar_persisteAlteracoes() {
		Usuario u = novo("Nome Antigo");
		dao.salvar(u);

		u.setNome("Nome Novo");
		dao.editar(u);

		assertEquals("Nome Novo", dao.buscar(u.getCodigo()).getNome());
	}

	@Test
	public void listarParametro_filtraPorLoginExato() {
		Usuario u = novo("Login Exato");
		dao.salvar(u);

		Usuario filtro = new Usuario();
		filtro.setUsuario(u.getUsuario());

		List<Usuario> resultado = dao.listarParametro(filtro, false);

		assertEquals(1, resultado.size());
		assertEquals(u.getCodigo(), resultado.get(0).getCodigo());
	}

	@Test
	public void listarParametro_loginInexistente_retornaVazio() {
		Usuario filtro = new Usuario();
		filtro.setUsuario("login_que_nao_existe_" + System.nanoTime());

		assertTrue(dao.listarParametro(filtro, false).isEmpty());
	}

	// RACE-01: a UNIQUE do banco (nao so a checagem em Java) e a trava de verdade contra 2 cadastros simultaneos.
	@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
	public void salvar_loginDuplicado_bancoRejeitaMesmoSemChecagemEmJava() {
		String login = "login_dup_" + System.nanoTime();
		dao.salvar(new Usuario("Primeiro", login, "s", "usr", "primeiro_" + System.nanoTime() + "@x.com"));

		// mesmo login, email diferente -- simula a JANELA de corrida onde a checagem em Java ja tinha passado pros dois.
		dao.salvar(new Usuario("Segundo", login, "s", "usr", "segundo_" + System.nanoTime() + "@x.com"));
	}

	@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
	public void salvar_emailDuplicado_bancoRejeita() {
		String email = "duplicado_" + System.nanoTime() + "@x.com";
		Usuario primeiro = new Usuario("Primeiro", "login_a_" + System.nanoTime(), "s", "usr", email);
		dao.salvar(primeiro);

		Usuario segundo = new Usuario("Segundo", "login_b_" + System.nanoTime(), "s", "usr", email);
		dao.salvar(segundo);
	}
}
