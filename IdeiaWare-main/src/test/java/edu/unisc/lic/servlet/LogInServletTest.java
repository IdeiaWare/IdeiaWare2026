package edu.unisc.lic.servlet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 2 (2026-07-05): LogInServlet -- infra de auth core, nunca teve teste.
 */
public class LogInServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String login, String senhaPlana) {
		Usuario u = new Usuario("Usuario Teste", login, "x", "usr", login + "@x.com");
		u.setSenha(senhaPlana, true);
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(String login, String senha) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter("usuario")).thenReturn(login);
		when(request.getParameter("senha")).thenReturn(senha);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		return request;
	}

	@Test
	public void loginValido_criaSessaoEForwardParaWait() throws Exception {
		String login = "login_" + System.nanoTime();
		novoUsuario(login, "senha123");

		HttpServletRequest request = mockRequest(login, "senha123");
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogInServlet().doPost(request, response);

		verify(session).setAttribute(eq("codigoUsuario"), org.mockito.ArgumentMatchers.any());
		verify(request.getRequestDispatcher("wait.jsp")).forward(request, response);
	}

	@Test
	public void senhaErrada_naoAbreSessaoEForwardParaLogin() throws Exception {
		String login = "login_" + System.nanoTime();
		novoUsuario(login, "senha123");

		HttpServletRequest request = mockRequest(login, "senhaErrada");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogInServlet().doPost(request, response);

		verify(request).setAttribute("resposta", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);
		verify(request, org.mockito.Mockito.never()).getSession(true);
	}

	@Test
	public void usuarioInexistente_respostaEForwardParaLogin() throws Exception {
		HttpServletRequest request = mockRequest("nao_existe_" + System.nanoTime(), "qualquer");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogInServlet().doPost(request, response);

		verify(request).setAttribute("resposta", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);
	}

	@Test
	public void get_naoLogaERedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogInServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
