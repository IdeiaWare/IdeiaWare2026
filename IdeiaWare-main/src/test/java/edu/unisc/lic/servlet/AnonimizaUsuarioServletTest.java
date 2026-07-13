package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 2: AnonimizaUsuarioServlet (RKM-04/SEC-22/LGPD).
public class AnonimizaUsuarioServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String senhaPlana) {
		Usuario u = new Usuario("Usuario Teste", "login_" + System.nanoTime(), "x", "usr", "email_" + System.nanoTime() + "@x.com");
		u.setSenha(senhaPlana, true);
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String senhaAtual) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		when(request.getParameter("senhaAtual")).thenReturn(senhaAtual);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		return request;
	}

	private HttpServletResponse mockResponse() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
		return response;
	}

	@Test
	public void senhaAtualVazia_respostaSenhaInvalidaSemAnonimizar() throws Exception {
		Usuario u = novoUsuario("senha123");
		HttpServletRequest request = mockRequest(u.getCodigo(), "");
		HttpServletResponse response = mockResponse();

		new AnonimizaUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaSenhaInvalida", true);
		verify(request.getRequestDispatcher("index-perfil.jsp")).forward(request, response);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("N", recarregado.getAnonimizado());
	}

	@Test
	public void senhaAtualErrada_respostaSenhaInvalidaSemAnonimizar() throws Exception {
		Usuario u = novoUsuario("senha123");
		HttpServletRequest request = mockRequest(u.getCodigo(), "senhaErrada");
		HttpServletResponse response = mockResponse();

		new AnonimizaUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaSenhaInvalida", true);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("N", recarregado.getAnonimizado());
	}

	@Test
	public void senhaCorreta_anonimizaEInvalidaSessao() throws Exception {
		Usuario u = novoUsuario("senha123");
		HttpServletRequest request = mockRequest(u.getCodigo(), "senha123");
		HttpSession session = request.getSession(true);
		HttpServletResponse response = mockResponse();

		new AnonimizaUsuarioServlet().doPost(request, response);

		verify(session).invalidate();
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("S", recarregado.getAnonimizado());
	}

	@Test
	public void get_bloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AnonimizaUsuarioServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
