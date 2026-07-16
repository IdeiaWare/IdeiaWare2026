package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 2/SEC-19: ResetPasswordServlet -- resposta e a MESMA exista ou nao o e-mail (anti-enumeracao).
public class ResetPasswordServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String email) {
		Usuario u = new Usuario("Usuario Teste", "login_" + System.nanoTime(), "senhaAntiga", "usr", email);
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(String email) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter("email")).thenReturn(email);
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
	public void emailExistente_geraTokenDeResetENaoTrocaSenhaAinda() throws Exception {
		String email = "existe_" + System.nanoTime() + "@x.com";
		Usuario u = novoUsuario(email);
		String senhaAntigaHash = u.getSenha();

		HttpServletRequest request = mockRequest(email);
		HttpServletResponse response = mockResponse();

		new ResetPasswordServlet().doPost(request, response);

		verify(request).setAttribute("SucessoRedefinicaoSenha", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);

		// RESET-TOKEN: a senha so muda quando o usuario confirma via RedefinirSenhaServlet.
		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("senha nao deve mudar so por pedir o reset", senhaAntigaHash, recarregado.getSenha());
		assertNotNull("token de reset deve ter sido gerado", recarregado.getResetTokenHash());
		assertNotNull("expiracao do token deve ter sido setada", recarregado.getResetTokenExpira());
		assertTrue("expiracao deve ser no futuro", recarregado.getResetTokenExpira().after(new Date()));
	}

	@Test
	public void emailInexistente_mesmaRespostaDeSucesso_antiEnumeracao() throws Exception {
		HttpServletRequest request = mockRequest("nao_existe_" + System.nanoTime() + "@x.com");
		HttpServletResponse response = mockResponse();

		new ResetPasswordServlet().doPost(request, response);

		verify(request).setAttribute("SucessoRedefinicaoSenha", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);
	}

	@Test
	public void get_bloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ResetPasswordServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
