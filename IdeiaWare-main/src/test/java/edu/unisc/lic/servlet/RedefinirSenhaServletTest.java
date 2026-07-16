package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import edu.unisc.lic.classes.TokenReset;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

// RESET-TOKEN: 2a etapa do reset de senha -- valida token+expiracao e so troca a senha na confirmacao.
public class RedefinirSenhaServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario usuarioComTokenValido() {
		Usuario u = new Usuario("Usuario Teste", "login_" + System.nanoTime(), "senhaAntiga", "usr",
				"x" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private String gerarESalvarToken(Usuario u, long deltaMsExpiracao) {
		String token = TokenReset.gerar();
		u.setResetTokenHash(TokenReset.hash(token));
		u.setResetTokenExpira(new Date(System.currentTimeMillis() + deltaMsExpiracao));
		usuarioDAO.editar(u);
		return token;
	}

	private HttpServletRequest mockRequest() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		return request;
	}

	@Test
	public void get_tokenValido_mostraFormularioComToken() throws Exception {
		Usuario u = usuarioComTokenValido();
		String token = gerarESalvarToken(u, 60_000);

		HttpServletRequest request = mockRequest();
		when(request.getParameter("token")).thenReturn(token);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new RedefinirSenhaServlet().doGet(request, response);

		verify(request).setAttribute("token", token);
		verify(request.getRequestDispatcher("redefinir-senha.jsp")).forward(request, response);
	}

	@Test
	public void get_tokenInexistente_marcaInvalido() throws Exception {
		HttpServletRequest request = mockRequest();
		when(request.getParameter("token")).thenReturn("token-que-nao-existe");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new RedefinirSenhaServlet().doGet(request, response);

		verify(request).setAttribute("tokenInvalido", true);
	}

	@Test
	public void get_tokenExpirado_marcaInvalido() throws Exception {
		Usuario u = usuarioComTokenValido();
		String token = gerarESalvarToken(u, -60_000); // expirou ha 1 minuto

		HttpServletRequest request = mockRequest();
		when(request.getParameter("token")).thenReturn(token);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new RedefinirSenhaServlet().doGet(request, response);

		verify(request).setAttribute("tokenInvalido", true);
	}

	@Test
	public void post_tokenValidoESenhasConferem_trocaSenhaEInvalidaToken() throws Exception {
		Usuario u = usuarioComTokenValido();
		String senhaAntigaHash = u.getSenha();
		String token = gerarESalvarToken(u, 60_000);

		HttpServletRequest request = mockRequest();
		when(request.getParameter("token")).thenReturn(token);
		when(request.getParameter("senha")).thenReturn("NovaSenha123!");
		when(request.getParameter("senha2")).thenReturn("NovaSenha123!");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new RedefinirSenhaServlet().doPost(request, response);

		verify(request).setAttribute("SenhaRedefinidaComSucesso", true);
		verify(request.getRequestDispatcher("login.jsp")).forward(request, response);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertNotEquals("senha deve ter mudado", senhaAntigaHash, recarregado.getSenha());
		assertNull("token deve ser invalidado apos o uso (uso unico)", recarregado.getResetTokenHash());
		assertNull(recarregado.getResetTokenExpira());
	}

	@Test
	public void post_senhasNaoConferem_naoTrocaSenhaNemInvalidaToken() throws Exception {
		Usuario u = usuarioComTokenValido();
		String senhaAntigaHash = u.getSenha();
		String token = gerarESalvarToken(u, 60_000);

		HttpServletRequest request = mockRequest();
		when(request.getParameter("token")).thenReturn(token);
		when(request.getParameter("senha")).thenReturn("NovaSenha123!");
		when(request.getParameter("senha2")).thenReturn("Diferente456!");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new RedefinirSenhaServlet().doPost(request, response);

		verify(request).setAttribute("senhasNaoConferem", true);
		verify(request.getRequestDispatcher("redefinir-senha.jsp")).forward(request, response);

		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("senha nao deve mudar se a confirmacao nao bate", senhaAntigaHash, recarregado.getSenha());
	}

	@Test
	public void post_tokenJaUsado_naoDeixaReusarOLink() throws Exception {
		Usuario u = usuarioComTokenValido();
		String token = gerarESalvarToken(u, 60_000);

		HttpServletRequest primeiraRequest = mockRequest();
		when(primeiraRequest.getParameter("token")).thenReturn(token);
		when(primeiraRequest.getParameter("senha")).thenReturn("NovaSenha123!");
		when(primeiraRequest.getParameter("senha2")).thenReturn("NovaSenha123!");
		new RedefinirSenhaServlet().doPost(primeiraRequest, mock(HttpServletResponse.class));

		HttpServletRequest segundaRequest = mockRequest();
		when(segundaRequest.getParameter("token")).thenReturn(token);
		when(segundaRequest.getParameter("senha")).thenReturn("OutraSenha789!");
		when(segundaRequest.getParameter("senha2")).thenReturn("OutraSenha789!");
		new RedefinirSenhaServlet().doPost(segundaRequest, mock(HttpServletResponse.class));

		verify(segundaRequest).setAttribute("tokenInvalido", true);
		assertFalse("segunda tentativa com o mesmo token nao pode ser aceita",
				usuarioDAO.buscar(u.getCodigo()).checaSenha("OutraSenha789!"));
	}
}
