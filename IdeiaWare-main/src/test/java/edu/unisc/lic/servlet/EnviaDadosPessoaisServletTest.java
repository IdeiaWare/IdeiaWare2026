package edu.unisc.lic.servlet;

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

/**
 * TEST-04, Tier 2 (2026-07-05): EnviaDadosPessoaisServlet (LGPD). Achado nesta rodada:
 * o GET nao era bloqueado (unico do grupo Tier 2 sem SEC-18) -- corrigido pra
 * POST-only, testado abaixo.
 */
public class EnviaDadosPessoaisServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario() {
		Usuario u = new Usuario("Usuario Teste", "login_" + System.nanoTime(), "x", "usr", "email_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		when(request.getContextPath()).thenReturn("");
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
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null);
		HttpServletResponse response = mockResponse();

		new EnviaDadosPessoaisServlet().doPost(request, response);

		verify(response).sendRedirect("/login.jsp");
	}

	@Test
	public void usuarioLogado_forwardParaPerfilComResultadoDoEnvio() throws Exception {
		Usuario u = novoUsuario();
		HttpServletRequest request = mockRequest(u.getCodigo());
		HttpServletResponse response = mockResponse();

		new EnviaDadosPessoaisServlet().doPost(request, response);

		// sem SENDGRID_API_KEY no ambiente de teste, EnvioEmail.EnviaEmail retorna false
		// sem tentar rede -- exercita o caminho de erro amigavel (nao uma excecao crua).
		verify(request).setAttribute("ErroEnvioEmail", true);
		verify(request.getRequestDispatcher("index-perfil.jsp")).forward(request, response);
	}

	@Test
	public void get_agoraBloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviaDadosPessoaisServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
