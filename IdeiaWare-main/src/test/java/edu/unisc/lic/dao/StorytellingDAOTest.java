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

// TEST-04: reativado do scratch @Ignore original; Storytelling.usuario/ideia sao @OneToOne, cada teste usa dados novos.
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

		// TEST-04: H2 e compartilhado entre TODAS as classes na mesma JVM -- nao assume exclusividade global do status "FN", so que o registro esta no resultado e nada tem status diferente.
		assertTrue("o storytelling finalizado criado neste teste deve estar no resultado",
				resultado.stream().anyMatch(s -> s.getCodigo().equals(finalizado.getCodigo())));
		assertTrue("todo resultado do filtro deve ter status FN",
				resultado.stream().allMatch(s -> "FN".equals(s.getStatus())));
	}
}
