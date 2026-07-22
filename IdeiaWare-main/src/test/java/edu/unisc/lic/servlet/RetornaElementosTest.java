package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// RetornaElementos: devolve TODOS os elementos do storytelling ativo na sessao -- sem storytellingId -> 400 (STM-04).
public class RetornaElementosTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia RetornaElementos", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private Storytelling novoStorytelling(Usuario autor, Ideia ideia) {
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Long storytellingId) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		return request;
	}

	private StringWriter mockResponseWriter(HttpServletResponse response) throws Exception {
		StringWriter sw = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(sw));
		return sw;
	}

	@Test
	public void semStorytellingIdNaSessao_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(null);
		HttpServletResponse response = mock(HttpServletResponse.class);
		mockResponseWriter(response);

		new RetornaElementos().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void semElementos_retornaListaVazia() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		HttpServletRequest request = mockRequest(st.getCodigo());
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaElementos().doGet(request, response);

		JsonArray lista = JsonParser.parseString(sw.toString()).getAsJsonArray();
		assertEquals(0, lista.size());
	}

	@Test
	public void comElementosDeVariosTipos_retornaTodosDoStorytelling() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		elementosStorytellingDAO.salvar(new ElementosStorytelling(st, "AUD", "audio-data", 0, 0, 0, 0));
		elementosStorytellingDAO.salvar(new ElementosStorytelling(st, "IMG", "", 1, 1, 10, 10));

		ElementosStorytelling texto = new ElementosStorytelling();
		texto.setStorytelling(st);
		texto.setTipo("TXT");
		texto.setCaminho("texto");
		texto.setInformacaoTexto("ola");
		elementosStorytellingDAO.salvar(texto);

		Usuario autorOutro = novoUsuario("AutorOutro");
		Ideia ideiaOutra = novaIdeia(autorOutro);
		Storytelling stOutro = novoStorytelling(autorOutro, ideiaOutra);
		elementosStorytellingDAO.salvar(new ElementosStorytelling(stOutro, "IMG", "", 0, 0, 0, 0));

		HttpServletRequest request = mockRequest(st.getCodigo());
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaElementos().doGet(request, response);

		JsonArray lista = JsonParser.parseString(sw.toString()).getAsJsonArray();
		assertEquals(3, lista.size());
	}
}
