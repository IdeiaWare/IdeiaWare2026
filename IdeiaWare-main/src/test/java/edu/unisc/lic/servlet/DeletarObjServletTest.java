package edu.unisc.lic.servlet;

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
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: DeletarObjServlet (STM-05/STM-06).
public class DeletarObjServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final ElementosStorytellingDAO elementosDAO = new ElementosStorytellingDAO();

	private Storytelling novoStorytelling() {
		Usuario autor = new Usuario("Autor", "autor_" + System.nanoTime(), "s", "usr", "autor_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);
		Ideia ideia = new Ideia(autor, "Ideia", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(st);
		return st;
	}

	private ElementosStorytelling novoElemento(Storytelling st) {
		ElementosStorytelling e = new ElementosStorytelling();
		e.setStorytelling(st);
		e.setTipo("texto");
		e.setCaminho("");
		e.setX(0d);
		e.setY(0d);
		elementosDAO.salvar(e);
		return e;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, Long storytellingId, String bodyJson) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
			when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		}
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(bodyJson == null ? "" : bodyJson)));
		return request;
	}

	@Test
	public void semSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, null, "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarObjServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void corpoVazio_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(1L, 1L, "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarObjServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void elementoInexistente_retorna404() throws Exception {
		HttpServletRequest request = mockRequest(1L, 1L, "999999");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarObjServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void elementoDeOutroStorytelling_retorna403() throws Exception {
		Storytelling st = novoStorytelling();
		ElementosStorytelling elemento = novoElemento(st);

		HttpServletRequest request = mockRequest(1L, 999999L, elemento.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarObjServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		org.junit.Assert.assertNotNull("elemento nao deve ter sido excluido", elementosDAO.buscar(elemento.getCodigo()));
	}

	@Test
	public void elementoDoProprioStorytelling_excluiComSucesso() throws Exception {
		Storytelling st = novoStorytelling();
		ElementosStorytelling elemento = novoElemento(st);

		HttpServletRequest request = mockRequest(1L, st.getCodigo(), elemento.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarObjServlet().doPost(request, response);

		org.junit.Assert.assertNull("elemento deve ter sido excluido", elementosDAO.buscar(elemento.getCodigo()));
	}
}
