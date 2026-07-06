package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04 (2026-07-03), Tier 1: EntrarDetalheServlet -- o fix aqui foi parar de
 * confiar no parametro "lider" vindo do cliente e calcular no servidor. Nao ha
 * bloqueio de acesso (qualquer logado ve o detalhe), so o flag "lider" muda.
 */
public class EntrarDetalheServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Detalhe Servlet", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String codigo, Map<String, Object> sessionAttrs) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(anyString())).thenAnswer(inv -> sessionAttrs.get(inv.getArgument(0, String.class)));
		org.mockito.Mockito.doAnswer(inv -> sessionAttrs.put(inv.getArgument(0), inv.getArgument(1)))
				.when(session).setAttribute(anyString(), org.mockito.ArgumentMatchers.any());
		sessionAttrs.put("codigoUsuario", codigoUsuario);
		when(request.getParameter("codigo")).thenReturn(codigo);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarDetalheServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("login.jsp"));
	}

	@Test
	public void codigoInvalido_redirecionaListaIdeia() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarDetalheServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("lista-ideia.jsp"));
	}

	@Test
	public void semVinculo_liderCalculadoComoN() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Usuario visitante = novoUsuario("Visitante"); // logado, sem vinculo com esta ideia

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(visitante.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarDetalheServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("detalhes-ideia.jsp"));
		assertEquals("N", attrs.get("lider"));
	}

	@Test
	public void liderDeVerdade_liderCalculadoComoS_naoConfiaNoParametroDoCliente() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		// mesmo que o cliente tentasse mandar lider=N por parametro, o servlet nem le esse
		// parametro mais -- e exatamente o fix (RET-14/COLM-05).
		when(request.getParameter("lider")).thenReturn("N");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarDetalheServlet().doGet(request, response);

		assertEquals("S", attrs.get("lider"));
	}
}
