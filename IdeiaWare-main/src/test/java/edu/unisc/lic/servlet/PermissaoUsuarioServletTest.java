package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 2 (2026-07-05): PermissaoUsuarioServlet (RKM-01) -- so admin pode
 * promover/rebaixar outros usuarios.
 */
public class PermissaoUsuarioServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(Long codigoUsuarioLogado, String codigoAlterar, Boolean gestor) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuarioLogado == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuarioLogado);
		}
		when(request.getParameter("codigoAlterar")).thenReturn(codigoAlterar);
		Map<String, String[]> paramMap = new HashMap<>();
		if (gestor != null && gestor) {
			paramMap.put("gestor", new String[] { "gestor" });
		}
		when(request.getParameterMap()).thenReturn(paramMap);
		when(request.getContextPath()).thenReturn("");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doPost(request, response);

		verify(response).sendRedirect("/login.jsp");
	}

	@Test
	public void usuarioNaoAdmin_redirecionaParaIndex() throws Exception {
		Usuario naoAdmin = novoUsuario("NaoAdmin", "usr");
		HttpServletRequest request = mockRequest(naoAdmin.getCodigo(), "1", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doPost(request, response);

		verify(response).sendRedirect("/index.jsp");
	}

	@Test
	public void admin_promoveUsuarioParaGestor() throws Exception {
		Usuario admin = novoUsuario("Admin", "adm");
		Usuario alvo = novoUsuario("Alvo", "usr");
		HttpServletRequest request = mockRequest(admin.getCodigo(), alvo.getCodigo().toString(), true);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doPost(request, response);

		Usuario recarregado = usuarioDAO.buscar(alvo.getCodigo());
		assertEquals("adm", recarregado.getPermissao());
	}

	@Test
	public void admin_rebaixaUsuarioParaColaborador() throws Exception {
		Usuario admin = novoUsuario("Admin2", "adm");
		Usuario alvo = novoUsuario("Alvo2", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), alvo.getCodigo().toString(), false);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doPost(request, response);

		Usuario recarregado = usuarioDAO.buscar(alvo.getCodigo());
		assertEquals("col", recarregado.getPermissao());
	}

	@Test
	public void codigoAlterarInvalido_forwardParaGerenciarUsuarios() throws Exception {
		Usuario admin = novoUsuario("Admin3", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "abc", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doPost(request, response);

		verify(request.getRequestDispatcher("gerenciar-usuarios.jsp")).forward(request, response);
	}

	@Test
	public void get_bloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new PermissaoUsuarioServlet().doGet(request, response);

		verify(response).sendRedirect("/login.jsp");
	}
}
