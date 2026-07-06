package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04: reativado a partir do scratch @Ignore original. Storytelling.usuario e
 * Storytelling.ideia sao @OneToOne (1 storytelling por usuario e 1 por ideia) --
 * cada teste usa Usuario/Ideia novos pra nao esbarrar nas constraints UNIQUE.
 */
public class StorytellingDAOTest {

	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Story", "autor_story_" + System.nanoTime(), "s", "usr", "autor_story_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Story", "Descricao", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEBuscar_preservaUsuarioIdeiaEStatus() {
		Ideia ideia = novaIdeiaSalva();
		Storytelling s = new Storytelling(ideia.getUsuario(), ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(s);

		assertNotNull("codigo gerado ao salvar", s.getCodigo());

		Storytelling carregado = storytellingDAO.buscar(s.getCodigo());
		assertNotNull(carregado);
		assertEquals("AB", carregado.getStatus());
		assertEquals(ideia.getCodigo(), carregado.getIdeia().getCodigo());
		assertEquals(ideia.getUsuario().getCodigo(), carregado.getUsuario().getCodigo());
	}

	@Test
	public void listarParametro_filtraPorIdeia() {
		Ideia ideiaA = novaIdeiaSalva();
		Ideia ideiaB = novaIdeiaSalva();

		storytellingDAO.salvar(new Storytelling(ideiaA.getUsuario(), ideiaA, Data.horaAtual(), "AB"));
		storytellingDAO.salvar(new Storytelling(ideiaB.getUsuario(), ideiaB, Data.horaAtual(), "AB"));

		Storytelling filtro = new Storytelling();
		filtro.setIdeia(ideiaA);

		List<Storytelling> resultado = storytellingDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(ideiaA.getCodigo(), resultado.get(0).getIdeia().getCodigo());
	}

	@Test
	public void listarParametro_filtraPorStatus() {
		Ideia ideiaAtiva = novaIdeiaSalva();
		Ideia ideiaFinalizada = novaIdeiaSalva();

		storytellingDAO.salvar(new Storytelling(ideiaAtiva.getUsuario(), ideiaAtiva, Data.horaAtual(), "AB"));
		Storytelling finalizado = new Storytelling(ideiaFinalizada.getUsuario(), ideiaFinalizada, Data.horaAtual(), "FN");
		storytellingDAO.salvar(finalizado);

		Storytelling filtro = new Storytelling();
		filtro.setStatus("FN");

		List<Storytelling> resultado = storytellingDAO.listarParametro(filtro);

		// TEST-04 (2026-07-03): nao assume exclusividade GLOBAL do status "FN" -- o H2 de
		// teste e compartilhado entre TODAS as classes de teste na mesma JVM (DB_CLOSE_DELAY=-1
		// + SessionFactory static), entao outra classe (ex.: EntrarStorytellingServletTest) pode
		// ja ter criado outro Storytelling "FN" -- foi exatamente isso que quebrou o build no
		// Docker (assertEquals(1, ...) virou 2). O que este teste precisa provar e: (1) o
		// registro criado aqui esta no resultado, (2) NENHUM resultado tem status diferente de
		// "FN" (o filtro realmente exclui o "AB" acima) -- nao "sou o unico FN do banco todo".
		assertTrue("o storytelling finalizado criado neste teste deve estar no resultado",
				resultado.stream().anyMatch(s -> s.getCodigo().equals(finalizado.getCodigo())));
		assertTrue("todo resultado do filtro deve ter status FN",
				resultado.stream().allMatch(s -> "FN".equals(s.getStatus())));
	}
}
