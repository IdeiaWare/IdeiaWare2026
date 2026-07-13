package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

// TEST-04/K.8 #6: AlteraUsuarioServlet (RKM-04/SEC-22) -- edicao de perfil com checagem de e-mail duplicado via UNIQUE + catch (mesmo padrao do RACE-01).
public class AlteraUsuarioServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String senhaPlana) {
		Usuario u = new Usuario("Usuario Teste", "login_" + System.nanoTime(), "x", "usr", "email_" + System.nanoTime() + "@x.com");
		u.setSenha(senhaPlana, true);
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, Map<String, String> params) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);

		Map<String, String[]> paramMap = new HashMap<>();
		for (Map.Entry<String, String> e : params.entrySet()) {
			paramMap.put(e.getKey(), new String[] { e.getValue() });
			when(request.getParameter(e.getKey())).thenReturn(e.getValue());
		}
		when(request.getParameterMap()).thenReturn(paramMap);

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
	public void trocaEmailParaUmJaExistente_bloqueiaComRespostaEmailCadastrado() throws Exception {
		Usuario outro = novoUsuario("x");
		Usuario u = novoUsuario("x");

		Map<String, String> params = new HashMap<>();
		params.put("email", outro.getEmail());
		HttpServletRequest request = mockRequest(u.getCodigo(), params);
		HttpServletResponse response = mockResponse();

		new AlteraUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaEmailCadastrado", true);
		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("email nao deve ter sido trocado", u.getEmail(), recarregado.getEmail());
	}

	@Test
	public void trocaSenhaComSenhaAtualErrada_bloqueiaComRespostaSenhaInvalida() throws Exception {
		Usuario u = novoUsuario("senhaCerta");

		Map<String, String> params = new HashMap<>();
		params.put("senhaAtual", "senhaErrada");
		params.put("senhaNova", "novaSenha1");
		params.put("senha2", "novaSenha1");
		HttpServletRequest request = mockRequest(u.getCodigo(), params);
		HttpServletResponse response = mockResponse();

		new AlteraUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaSenhaInvalida", true);
	}

	@Test
	public void trocaSenhaComConfirmacaoDiferente_bloqueiaComRespostaSenhasDiferentes() throws Exception {
		Usuario u = novoUsuario("senhaCerta");

		Map<String, String> params = new HashMap<>();
		params.put("senhaAtual", "senhaCerta");
		params.put("senhaNova", "novaSenha1");
		params.put("senha2", "outraCoisa");
		HttpServletRequest request = mockRequest(u.getCodigo(), params);
		HttpServletResponse response = mockResponse();

		new AlteraUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaSenhasDiferentes", true);
	}

	@Test
	public void trocaNomeValido_salvaComSucesso() throws Exception {
		Usuario u = novoUsuario("x");

		Map<String, String> params = new HashMap<>();
		params.put("nome", "Nome Novo");
		params.put("email", u.getEmail());
		HttpServletRequest request = mockRequest(u.getCodigo(), params);
		HttpServletResponse response = mockResponse();

		new AlteraUsuarioServlet().doPost(request, response);

		verify(request).setAttribute("respostaSucesso", true);
		Usuario recarregado = usuarioDAO.buscar(u.getCodigo());
		assertEquals("Nome Novo", recarregado.getNome());
		// TEST-04: header (index.jsp) le sessionScope.nomeUsuario, so gravado no login -- editar o nome persistia mas o header ficava desatualizado ate relogar.
		verify(request.getSession(true)).setAttribute("nomeUsuario", "Nome Novo");
	}

	@Test
	public void semParametrosDeAlteracao_naoQuebraEForwardParaPerfil() throws Exception {
		Usuario u = novoUsuario("x");

		HttpServletRequest request = mockRequest(u.getCodigo(), new HashMap<>());
		HttpServletResponse response = mockResponse();

		new AlteraUsuarioServlet().doPost(request, response);

		verify(request.getRequestDispatcher("index-perfil.jsp")).forward(request, response);
	}

	@Test
	public void doisUsuariosTrocandoParaOMesmoEmailSimultaneamente_soUmVence() throws Exception {
		Usuario u1 = novoUsuario("x");
		Usuario u2 = novoUsuario("x");
		String emailAlvo = "alvo_" + System.nanoTime() + "@x.com";

		Map<String, String> params1 = new HashMap<>();
		params1.put("email", emailAlvo);
		Map<String, String> params2 = new HashMap<>();
		params2.put("email", emailAlvo);

		CyclicBarrier largada = new CyclicBarrier(2);
		CountDownLatch fim = new CountDownLatch(2);
		AtomicReference<Throwable> escapouThread1 = new AtomicReference<>();
		AtomicReference<Throwable> escapouThread2 = new AtomicReference<>();

		new Thread(criarTarefa(u1.getCodigo(), params1, largada, fim, escapouThread1)).start();
		new Thread(criarTarefa(u2.getCodigo(), params2, largada, fim, escapouThread2)).start();

		assertTrue("as 2 threads devem terminar", fim.await(10, TimeUnit.SECONDS));
		assertNull("doPost nao deve deixar excecao escapar (thread 1)", escapouThread1.get());
		assertNull("doPost nao deve deixar excecao escapar (thread 2)", escapouThread2.get());

		long donosDoEmailAlvo = 0;
		Usuario u1Recarregado = usuarioDAO.buscar(u1.getCodigo());
		Usuario u2Recarregado = usuarioDAO.buscar(u2.getCodigo());
		if (emailAlvo.equals(u1Recarregado.getEmail())) {
			donosDoEmailAlvo++;
		}
		if (emailAlvo.equals(u2Recarregado.getEmail())) {
			donosDoEmailAlvo++;
		}
		assertEquals("so 1 dos 2 usuarios deve ter ficado com o e-mail, mesmo com a corrida", 1, donosDoEmailAlvo);
	}

	private Runnable criarTarefa(Long codigoUsuario, Map<String, String> params, CyclicBarrier largada,
			CountDownLatch fim, AtomicReference<Throwable> escapou) {
		return () -> {
			try {
				HttpServletRequest request = mockRequest(codigoUsuario, params);
				HttpServletResponse response = mockResponse();
				largada.await(5, TimeUnit.SECONDS);
				new AlteraUsuarioServlet().doPost(request, response);
			} catch (Exception t) {
				escapou.set(t);
			} finally {
				fim.countDown();
			}
		};
	}

	@Test
	public void get_bloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AlteraUsuarioServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
