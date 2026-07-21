package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
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

// InserirTexto: adiciona elemento de texto ao quadro do storytelling ativo na sessao -- sem storytellingId -> 400 (STM-04).
public class InserirTextoTest {

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
		Ideia ideia = new Ideia(autor, "Ideia InserirTexto", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
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
		String json = "{\"x\":1,\"y\":2,\"fonte\":\"Arial\",\"tamanho\":18,\"cor\":\"#123456\",\"informacaoTexto\":\"ola\"}";
		HttpServletRequest request = mockRequest(null, json);
		HttpServletResponse response = mock(HttpServletResponse.class);
		mockResponseWriter(response);

		new InserirTexto().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void dadosValidos_criaElementoTextoComCamposCorretos() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		String json = "{\"x\":1.5,\"y\":2.5,\"fonte\":\"Arial\",\"tamanho\":18,\"cor\":\"#123456\","
				+ "\"informacaoTexto\":\"Ola mundo\"}";
		HttpServletRequest request = mockRequest(st.getCodigo(), json);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new InserirTexto().doPost(request, response);

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		List<ElementosStorytelling> lista = elementosStorytellingDAO.listarParametro(filtro);
		assertEquals(1, lista.size());

		ElementosStorytelling criado = lista.get(0);
		assertEquals("TXT", criado.getTipo());
		assertEquals("texto", criado.getCaminho());
		assertEquals(1.5, criado.getX(), 0.001);
		assertEquals(2.5, criado.getY(), 0.001);
		assertEquals("Arial", criado.getFonte());
		assertEquals(18, criado.getTamanhoFonte());
		assertEquals("#123456", criado.getCorFonte());
		assertEquals("Ola mundo", criado.getInformacaoTexto());

		JsonObject resposta = JsonParser.parseString(sw.toString()).getAsJsonObject();
		assertEquals(criado.getCodigo().longValue(), resposta.get("codigo").getAsLong());
		assertEquals("Ola mundo", resposta.get("informacaoTexto").getAsString());
	}

	@Test
	public void storytellingJaFinalizado_bloqueiaInsercao() throws Exception { // UX-STORY-ETAPA-TRAVADA
		Usuario autor = novoUsuario("AutorFinalizado");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);
		st.setStatus(StatusIdeia.FINALIZADO);
		storytellingDAO.editar(st);

		String json = "{\"x\":1,\"y\":2,\"fonte\":\"Arial\",\"tamanho\":18,\"cor\":\"#123456\",\"informacaoTexto\":\"tarde\"}";
		HttpServletRequest request = mockRequest(st.getCodigo(), json);
		HttpServletResponse response = mock(HttpServletResponse.class);
		mockResponseWriter(response);

		new InserirTexto().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		assertEquals(0, elementosStorytellingDAO.listarParametro(filtro).size());
	}
}
