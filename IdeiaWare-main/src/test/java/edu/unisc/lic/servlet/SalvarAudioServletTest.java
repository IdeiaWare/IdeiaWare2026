package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

// SalvarAudioServlet: salva/substitui o audio unico do storytelling ativo -- blindagens de sessao/id/existencia (ver "BLINDAGEM" no servlet).
public class SalvarAudioServletTest {

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
		Ideia ideia = new Ideia(autor, "Ideia SalvarAudio", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private Storytelling novoStorytelling(Usuario autor, Ideia ideia) {
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Object storytellingIdAttr, String body) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingIdAttr);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
		return request;
	}

	@Test
	public void corpoVazio_naoFazNadaNemErro() throws Exception {
		HttpServletRequest request = mockRequest(null, "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarAudioServlet().doPost(request, response);

		verify(response, never()).setStatus(org.mockito.ArgumentMatchers.anyInt());
	}

	@Test
	public void semStorytellingIdNaSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "audio-base64-data");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarAudioServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void storytellingIdNaoNumerico_retorna400() throws Exception {
		HttpServletRequest request = mockRequest("abc", "audio-base64-data");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarAudioServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void storytellingInexistente_retorna404() throws Exception {
		HttpServletRequest request = mockRequest("999999999", "audio-base64-data");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarAudioServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void dadosValidos_substituiAudioAnteriorPeloNovo() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		ElementosStorytelling audioAntigo = new ElementosStorytelling(st, "AUD", "audio-antigo", 0, 0, 0, 0);
		elementosStorytellingDAO.salvar(audioAntigo);

		HttpServletRequest request = mockRequest(st.getCodigo().toString(), "audio-novo-base64");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarAudioServlet().doPost(request, response);

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		filtro.setTipo("AUD");
		List<ElementosStorytelling> lista = elementosStorytellingDAO.listarParametro(filtro);

		assertEquals(1, lista.size());
		assertEquals("audio-novo-base64", lista.get(0).getCaminho());
		verify(response, never()).setStatus(org.mockito.ArgumentMatchers.anyInt());
	}

	@Test
	public void storytellingJaFinalizado_bloqueiaSubstituicaoDeAudio() throws Exception { // UX-STORY-ETAPA-TRAVADA
		Usuario autor = novoUsuario("AutorFinalizado");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);
		st.setStatus(StatusIdeia.FINALIZADO);
		storytellingDAO.editar(st);

		ElementosStorytelling audioAntigo = new ElementosStorytelling(st, "AUD", "audio-antigo", 0, 0, 0, 0);
		elementosStorytellingDAO.salvar(audioAntigo);

		HttpServletRequest request = mockRequest(st.getCodigo().toString(), "audio-novo-base64");
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

		new SalvarAudioServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		filtro.setTipo("AUD");
		List<ElementosStorytelling> lista = elementosStorytellingDAO.listarParametro(filtro);
		assertEquals("audio antigo nao deve ter sido substituido", "audio-antigo", lista.get(0).getCaminho());
	}
}
