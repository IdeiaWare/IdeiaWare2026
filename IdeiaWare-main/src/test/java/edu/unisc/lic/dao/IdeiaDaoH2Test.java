package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-03: integracao REAL de IdeiaDAO contra H2. Alem do round-trip, exercita o
 * relacionamento Ideia -> Usuario (@ManyToOne / FK), que so um teste com banco pega.
 */
public class IdeiaDaoH2Test {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario autorSalvo() {
		Usuario u = new Usuario("Autor", "autor_" + System.nanoTime(), "s", "usr", "a@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	@Test
	public void salvarIdeiaComAutor_eBuscar_preservaFkEStatus() {
		Usuario autor = autorSalvo();

		Ideia i = new Ideia(autor, "Minha Ideia", "Descricao da ideia",
				StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		i.setDtCriacao();
		ideiaDAO.salvar(i);

		assertNotNull("codigo da ideia gerado", i.getCodigo());

		Ideia carregada = ideiaDAO.buscar(i.getCodigo());
		assertNotNull(carregada);
		assertEquals("Minha Ideia", carregada.getTitulo());
		assertEquals(StatusIdeia.PENDENTE, carregada.getStatus());
		assertNotNull("a FK do usuario deve vir preenchida", carregada.getUsuario());
		assertEquals(autor.getCodigo(), carregada.getUsuario().getCodigo());
	}

	@Test
	public void editarStatus_persiste() {
		Usuario autor = autorSalvo();
		Ideia i = new Ideia(autor, "Ideia 2", "desc", StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		i.setDtCriacao();
		ideiaDAO.salvar(i);

		i.setStatus(StatusIdeia.VALIDADA);
		ideiaDAO.editar(i);

		assertEquals(StatusIdeia.VALIDADA, ideiaDAO.buscar(i.getCodigo()).getStatus());
	}
}
