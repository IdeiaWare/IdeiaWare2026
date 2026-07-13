package edu.unisc.lic.servlet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: DeleteCanvaServlet (CANM-04/CANM-09/CAN-04) -- guard IDOR + guard de open redirect no parametro 'context'.
public class DeleteCanvaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaDAO canvaDAO = new CanvaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Delete Canva", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, Long ideiaIdSessao, String canvaParam, String context) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
			when(session.getAttribute("ideiaId")).thenReturn(ideiaIdSessao);
		}
		when(request.getParameter("canva")).thenReturn(canvaParam);
		when(request.getParameter("context")).thenReturn(context);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, null, "1", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void canvaParamNaoNumerico_redirecionaEntrarCanva() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), "abc", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaServlet"));
	}

	@Test
	public void canvaDeOutraIdeia_naoExcluiIDOR() throws Exception {
		Usuario autorA = novoUsuario("AutorA");
		Ideia ideiaA = novaIdeia(autorA);
		Usuario autorB = novoUsuario("AutorB");
		Ideia ideiaB = novaIdeia(autorB);
		Canva canvaDeB = new Canva(ideiaB, "Post-it de B", "ffeb3b", "receita");
		canvaDAO.salvar(canvaDeB);

		HttpServletRequest request = mockRequest(autorA.getCodigo(), ideiaA.getCodigo(), canvaDeB.getCodigo().toString(), null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		assertNotNull("post-it de outra ideia nao deve ser excluido", canvaDAO.buscar(canvaDeB.getCodigo()));
		verify(response).sendRedirect(contains("EntrarCanvaServlet"));
	}

	@Test
	public void contextExterno_bloqueadoOpenRedirect() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		Canva canva = new Canva(ideia, "Post-it", "ffeb3b", "receita");
		canvaDAO.salvar(canva);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), canva.getCodigo().toString(),
				"http://evil.com");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		ArgumentCaptor<String> destino = ArgumentCaptor.forClass(String.class);
		verify(response).sendRedirect(destino.capture());
		org.junit.Assert.assertFalse("nao deve redirecionar para destino externo", destino.getValue().contains("evil.com"));
		org.junit.Assert.assertTrue(destino.getValue().contains("EntrarCanvaServlet"));
	}

	@Test
	public void contextComPrefixoValidoMasDestinoInexistente_caiNoDefault() throws Exception {
		// TEST-04: antes bastava comecar com "EntrarCanva" (mesmo inexistente) pra passar -- agora e uma lista fechada de destinos validos.
		Usuario autor = novoUsuario("Autor4");
		Ideia ideia = novaIdeia(autor);
		Canva canva = new Canva(ideia, "Post-it", "ffeb3b", "receita");
		canvaDAO.salvar(canva);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), canva.getCodigo().toString(),
				"EntrarCanvaXxxInexistente");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		verify(response).sendRedirect("EntrarCanvaServlet");
	}

	@Test
	public void canvaValido_excluiERedirecionaParaContextoValido() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);
		Canva canva = new Canva(ideia, "Post-it a excluir", "ffeb3b", "recurso");
		canvaDAO.salvar(canva);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), canva.getCodigo().toString(),
				"EntrarCanvaRecursoServlet");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeleteCanvaServlet().doPost(request, response);

		assertNull("post-it deve ter sido excluido", canvaDAO.buscar(canva.getCodigo()));
		verify(response).sendRedirect(contains("EntrarCanvaRecursoServlet"));
	}
}
