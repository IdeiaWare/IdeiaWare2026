package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04: reativado a partir do scratch @Ignore original.
 */
public class ElementosStorytellingDAOTest {

	private static final String TIPO_IMAGEM = "IMAGEM";

	private final ElementosStorytellingDAO elementosDAO = new ElementosStorytellingDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Storytelling novoStorytellingSalvo() {
		Usuario autor = new Usuario("Autor Elem", "autor_elem_" + System.nanoTime(), "s", "usr", "autor_elem_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Elem", "Descricao", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);

		Storytelling s = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(s);
		return s;
	}

	@Test
	public void salvarEBuscar_preservaTipoECaminho() {
		Storytelling s = novoStorytellingSalvo();
		ElementosStorytelling e = new ElementosStorytelling(s, TIPO_IMAGEM, "caminho/da/imagem.png", 10, 20, 100, 150);
		elementosDAO.salvar(e);

		assertNotNull("codigo gerado ao salvar", e.getCodigo());

		ElementosStorytelling carregado = elementosDAO.buscar(e.getCodigo());
		assertNotNull(carregado);
		assertEquals(TIPO_IMAGEM, carregado.getTipo());
		assertEquals("caminho/da/imagem.png", carregado.getCaminho());
		assertEquals(s.getCodigo(), carregado.getStorytelling().getCodigo());
	}

	@Test
	public void listarParametro_filtraPorStorytellingETipo() {
		Storytelling s = novoStorytellingSalvo();
		elementosDAO.salvar(new ElementosStorytelling(s, TIPO_IMAGEM, "img1.png", 0, 0, 10, 10));
		elementosDAO.salvar(new ElementosStorytelling(s, "TXT", "texto livre", 0, 0, 10, 10));

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(s);
		filtro.setTipo(TIPO_IMAGEM);

		List<ElementosStorytelling> resultado = elementosDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(TIPO_IMAGEM, resultado.get(0).getTipo());
	}

	@Test
	public void ultimoAdicionado_retornaOMaiorCodigo() {
		Storytelling s = novoStorytellingSalvo();
		elementosDAO.salvar(new ElementosStorytelling(s, TIPO_IMAGEM, "primeiro.png", 0, 0, 10, 10));
		ElementosStorytelling ultimo = new ElementosStorytelling(s, TIPO_IMAGEM, "ultimo.png", 0, 0, 10, 10);
		elementosDAO.salvar(ultimo);

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(s);
		filtro.setTipo(TIPO_IMAGEM);

		ElementosStorytelling resultado = elementosDAO.ultimoAdicionado(filtro);

		assertNotNull(resultado);
		assertEquals(ultimo.getCodigo(), resultado.getCodigo());
	}

	@Test
	public void ultimoAdicionado_semElementos_retornaNull() {
		Storytelling s = novoStorytellingSalvo();

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(s);
		filtro.setTipo(TIPO_IMAGEM);

		assertNull(elementosDAO.ultimoAdicionado(filtro));
	}

	// TEST-04/PERF-02 (2026-07-03): metodos em lote que eliminam o N+1 do autosave do
	// Storytelling (AutoSalvarStoryServlet) e do SalvarAudioServlet.
	@Test
	public void buscarPorCodigos_trazTodosNumaSoChamada() {
		Storytelling s = novoStorytellingSalvo();
		ElementosStorytelling e1 = new ElementosStorytelling(s, TIPO_IMAGEM, "a.png", 0, 0, 10, 10);
		ElementosStorytelling e2 = new ElementosStorytelling(s, TIPO_IMAGEM, "b.png", 0, 0, 10, 10);
		elementosDAO.salvar(e1);
		elementosDAO.salvar(e2);

		List<ElementosStorytelling> resultado = elementosDAO.buscarPorCodigos(
				Arrays.asList(e1.getCodigo(), e2.getCodigo()));

		assertEquals(2, resultado.size());
	}

	@Test
	public void buscarPorCodigos_listaVaziaOuNula_naoQuebraRetornaVazio() {
		assertTrue(elementosDAO.buscarPorCodigos(new ArrayList<>()).isEmpty());
		assertTrue(elementosDAO.buscarPorCodigos(null).isEmpty());
	}

	@Test
	public void salvarLote_persisteAsAlteracoesDeTodosNumaSoTransacao() {
		Storytelling s = novoStorytellingSalvo();
		ElementosStorytelling e1 = new ElementosStorytelling(s, TIPO_IMAGEM, "a.png", 0, 0, 10, 10);
		ElementosStorytelling e2 = new ElementosStorytelling(s, TIPO_IMAGEM, "b.png", 0, 0, 10, 10);
		elementosDAO.salvar(e1);
		elementosDAO.salvar(e2);

		e1.setX(99);
		e2.setX(88);
		elementosDAO.salvarLote(Arrays.asList(e1, e2));

		assertEquals(99, elementosDAO.buscar(e1.getCodigo()).getX(), 0.001);
		assertEquals(88, elementosDAO.buscar(e2.getCodigo()).getX(), 0.001);
	}

	@Test
	public void excluirTodos_removeTodosOsElementosDaLista() {
		Storytelling s = novoStorytellingSalvo();
		ElementosStorytelling e1 = new ElementosStorytelling(s, "AUD", "audio1.wav", 0, 0, 0, 0);
		ElementosStorytelling e2 = new ElementosStorytelling(s, "AUD", "audio2.wav", 0, 0, 0, 0);
		elementosDAO.salvar(e1);
		elementosDAO.salvar(e2);

		elementosDAO.excluirTodos(Arrays.asList(e1, e2));

		assertNull(elementosDAO.buscar(e1.getCodigo()));
		assertNull(elementosDAO.buscar(e2.getCodigo()));
	}
}
