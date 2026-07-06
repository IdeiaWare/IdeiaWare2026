package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04 (2026-07-03), Tier 1: EntrarCanvaBaseServlet (MNT-01) -- testado via uma
 * subclasse real (EntrarCanvaAtividadeServlet) ja que a base e abstrata. Cobre o
 * SRV-NPE-01 (null-check depois do buscar(ideiaId) da sessao).
 */
public class EntrarCanvaBaseServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaDAO canvaDAO = new CanvaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Canva Base", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Object ideiaIdNaSessao, HttpSession session) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("ideiaId")).thenReturn(ideiaIdNaSessao);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semIdeiaIdNaSessao_redirecionaListaCanvas() throws Exception {
		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mockRequest(null, session);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaAtividadeServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	public void ideiaIdNaSessaoNaoExisteMais_redirecionaListaCanvas() throws Exception {
		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mockRequest(999999L, session);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaAtividadeServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	public void ideiaValida_listaElementosDoTipoEEntraNaTelaCorreta() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		canvaDAO.salvar(new Canva(ideia, "Post-it de atividade", "ffeb3b", "atividade"));
		canvaDAO.salvar(new Canva(ideia, "Post-it de recurso", "ffeb3b", "recurso"));

		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mockRequest(ideia.getCodigo(), session);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaAtividadeServlet().doGet(request, response);

		verify(response).sendRedirect(contains("canva-atividades-principais.jsp"));

		org.mockito.ArgumentCaptor<Object> captor = org.mockito.ArgumentCaptor.forClass(Object.class);
		verify(session).setAttribute(org.mockito.ArgumentMatchers.eq("attributes"), captor.capture());
		@SuppressWarnings("unchecked")
		List<Canva> listados = (List<Canva>) captor.getValue();
		assertEquals("so o elemento do TIPO atividade, nao o de recurso", 1, listados.size());
	}
}
