package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-03: integracao REAL de IdeiaDAO contra H2. Alem do round-trip, exercita o
 * relacionamento Ideia -> Usuario (@ManyToOne / FK), que so um teste com banco pega.
 */
public class IdeiaDaoH2Test {

	private static final String TITULO_TESTE = "Titulo Unico X";

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario autorSalvo() {
		Usuario u = new Usuario("Autor", "autor_" + System.nanoTime(), "s", "usr", "autor_" + System.nanoTime() + "@x.com");
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

	// TEST-04 (2026-07-03): reativado do scratch @Ignore original (IdeiaDAOTest.excluir/listar).
	@Test
	public void excluir_removeORegistro() {
		Usuario autor = autorSalvo();
		Ideia i = new Ideia(autor, "Ideia a apagar", "desc", StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		i.setDtCriacao();
		ideiaDAO.salvar(i);
		Long codigo = i.getCodigo();

		ideiaDAO.excluir(i);

		assertNull(ideiaDAO.buscar(codigo));
	}

	@Test
	public void listarParametro_filtraPorStatusETitulo() {
		Usuario autor = autorSalvo();
		Ideia pendente = new Ideia(autor, TITULO_TESTE, "desc", StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		pendente.setDtCriacao();
		ideiaDAO.salvar(pendente);

		Ideia validada = new Ideia(autor, "Outro Titulo", "desc", StatusIdeia.VALIDADA, StatusIdeia.GRUPO_ABERTO);
		validada.setDtCriacao();
		ideiaDAO.salvar(validada);

		Ideia filtro = new Ideia();
		filtro.setTitulo(TITULO_TESTE);

		List<Ideia> resultado = ideiaDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(TITULO_TESTE, resultado.get(0).getTitulo());
	}
}
