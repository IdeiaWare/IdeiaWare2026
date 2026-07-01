package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.unisc.lic.domain.Usuario;

/**
 * TEST-03: integracao REAL de UsuarioDAO contra H2 (em memoria). Exercita o
 * round-trip de persistencia (salvar -> id gerado -> buscar) e o listar, usando o
 * mesmo GenericDAO/HibernateUtil de producao (so o banco e H2, via cfg de teste).
 */
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
}
