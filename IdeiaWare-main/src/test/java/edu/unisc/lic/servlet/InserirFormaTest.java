package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.unisc.lic.classes.Constantes;
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

// InserirForma: adiciona figura ao quadro do storytelling ativo na sessao -- sem storytellingId -> 400 (STM-04).
public class InserirFormaTest {

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
		Ideia ideia = new Ideia(autor, "Ideia InserirForma", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private Storytelling novoStorytelling(Usuario autor, Ideia ideia) {
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Long storytellingId, String json) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		return request;
	}

	private StringWriter mockResponseWriter(HttpServletResponse response) throws Exception {
		StringWriter sw = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(sw));
		return sw;
	}

	@Test
	public void semStorytellingIdNaSessao_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(null, "{\"tipo\":\"circulo\",\"x\":10,\"y\":20}");
		HttpServletResponse response = mock(HttpServletResponse.class);
		mockResponseWriter(response);

		new InserirForma().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void dadosValidos_criaElementoFormaComCaminhoECodigoNaResposta() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		HttpServletRequest request = mockRequest(st.getCodigo(), "{\"tipo\":\"circulo\",\"x\":10.0,\"y\":20.0}");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new InserirForma().doPost(request, response);

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		List<ElementosStorytelling> lista = elementosStorytellingDAO.listarParametro(filtro);
		assertEquals(1, lista.size());

		ElementosStorytelling criado = lista.get(0);
		assertEquals("IMG", criado.getTipo());
		assertEquals(Constantes.CAMINHO_FORMAS + "circulo.png", criado.getCaminho());
		assertEquals(10.0, criado.getX(), 0.001);
		assertEquals(20.0, criado.getY(), 0.001);
		assertEquals(128.0, criado.getAltura(), 0.001);
		assertEquals(128.0, criado.getLargura(), 0.001);

		JsonObject resposta = JsonParser.parseString(sw.toString()).getAsJsonObject();
		assertEquals(criado.getCodigo().longValue(), resposta.get("codigo").getAsLong());
		assertTrue(resposta.get("caminho").getAsString().endsWith("circulo.png"));
	}
}
