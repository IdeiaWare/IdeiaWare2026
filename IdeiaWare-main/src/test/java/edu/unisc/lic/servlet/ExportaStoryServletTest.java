package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 3: ExportaStoryServlet -- blindagem contra sessao sem storytellingId
 * (NPE), id nao-numerico (NumberFormatException) e corpo vazio, alem do fluxo feliz
 * que finaliza o Storytelling e avanca a Ideia para a Caixa de Ferramentas.
 */
public class ExportaStoryServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Storytelling novoStorytelling() {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = new Ideia(autor, "Ideia Story", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "DE");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Object storytellingId, String body) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body == null ? "" : body)));
		return request;
	}

	@Test
	public void corpoVazio_retorna400() throws Exception {
		Storytelling st = novoStorytelling();
		HttpServletRequest request = mockRequest(st.getCodigo(), "   ");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void semStorytellingIdNaSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void storytellingIdNaoNumerico_retorna400() throws Exception {
		HttpServletRequest request = mockRequest("abc", "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void storytellingInexistente_retorna404() throws Exception {
		HttpServletRequest request = mockRequest(999999L, "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void dadosValidos_finalizaStorytellingEAvancaIdeiaParaCaixaFerramentas() throws Exception {
		Storytelling st = novoStorytelling();
		HttpServletRequest request = mockRequest(st.getCodigo(), "base64PdfConteudoFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		Storytelling atualizado = storytellingDAO.buscar(st.getCodigo());
		assertEquals(StatusIdeia.FINALIZADO, atualizado.getStatus());
		assertEquals("base64PdfConteudoFake", atualizado.getCaminhoFinalizado());

		Ideia ideiaAtualizada = ideiaDAO.buscar(st.getIdeia().getCodigo());
		assertEquals(StatusIdeia.CAIXA_FERRAMENTAS, ideiaAtualizada.getStatus());
	}
}
