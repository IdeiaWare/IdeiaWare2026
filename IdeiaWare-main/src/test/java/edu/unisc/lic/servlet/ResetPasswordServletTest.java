package edu.unisc.lic.servlet;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 2 (2026-07-05): ResetPasswordServlet. SEC-19 (anti-enumeracao): a
 * resposta tem que ser a MESMA exista ou nao o e-mail. Sem SENDGRID_API_KEY no ambiente
 * de teste, EnvioEmail.EnviaEmail sempre retorna false sem tentar rede -- o que ja
 * exercita o caminho "envio falhou" do RET-14-EMAIL (resposta nao muda mesmo assim).
 */
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
	public void emailExistente_trocaSenhaEForwardComSucesso() throws Exception {
		String email = "existe_" + System.nanoTime() + "@x.com";
		Usuario u = novoUsuario(email);
		String senhaAntigaHash = u.getSenha();

		HttpServletRequest request = mockRequest(email);
		HttpServletResponse response = mockResponse();

		new ResetPasswordServlet().doPost(request, response);

		verify(request).setAttribute("SucessoRedefinicaoSenha", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertNotEquals("senha deve ter sido trocada mesmo com o envio de e-mail falhando (sem SENDGRID_API_KEY no teste)",
				senhaAntigaHash, recarregado.getSenha());
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
