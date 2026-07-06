package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import edu.unisc.lic.classes.AssinaturaCaixa;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 3: EntrarCaixaServlet (SEC-23) -- exige login + participacao na ideia
 * antes de assinar (HMAC) o cookie ideiaId consumido pelo Toolkit.
 */
public class EntrarCaixaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Caixa", "desc", StatusIdeia.CAIXA_FERRAMENTAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String ideiaIdParam, String usuarioNomeParam) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("ideiaId")).thenReturn(ideiaIdParam);
		when(request.getParameter("usuarioNome")).thenReturn(usuarioNomeParam);
		when(request.getContextPath()).thenReturn("");
		when(request.getServerName()).thenReturn("localhost");
		when(request.getServerPort()).thenReturn(8080);
		when(request.getScheme()).thenReturn("http");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCaixaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void ideiaIdAusenteOuInvalido_redirecionaListaCaixa() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCaixaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-caixa-de-ferramentas.jsp"));
	}

	@Test
	public void usuarioNaoParticipante_redirecionaListaCaixa() throws Exception {
		Usuario dono = novoUsuario("Dono");
		Ideia ideia = novaIdeia(dono);
		Usuario estranho = novoUsuario("Estranho");

		HttpServletRequest request = mockRequest(estranho.getCodigo(), ideia.getCodigo().toString(), null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCaixaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-caixa-de-ferramentas.jsp"));
	}

	@Test
	public void participante_assinaCookiesERedirecionaParaToolkit() throws Exception {
		Usuario dono = novoUsuario("Dono2");
		Ideia ideia = novaIdeia(dono);
		IdeiaUsuario vinculo = new IdeiaUsuario(dono, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(dono.getCodigo(), ideia.getCodigo().toString(), "Fulano");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCaixaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("http://localhost:8080/toolkit"));

		ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
		verify(response, times(3)).addCookie(captor.capture());
		List<Cookie> cookies = captor.getAllValues();

		Cookie ideiaIdCookie = cookies.stream().filter(c -> "ideiaId".equals(c.getName())).findFirst().orElse(null);
		Cookie sigCookie = cookies.stream().filter(c -> "ideiaSig".equals(c.getName())).findFirst().orElse(null);
		Cookie nomeCookie = cookies.stream().filter(c -> "usuarioNome".equals(c.getName())).findFirst().orElse(null);

		assertEquals(ideia.getCodigo().toString(), ideiaIdCookie.getValue());
		assertEquals(AssinaturaCaixa.assinar(ideia.getCodigo().toString()), sigCookie.getValue());
		assertEquals("Fulano", nomeCookie.getValue());
	}
}
