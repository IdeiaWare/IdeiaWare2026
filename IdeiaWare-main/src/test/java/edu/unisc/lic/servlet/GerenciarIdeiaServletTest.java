package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doAnswer;
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

// TEST-04, Tier 3: GerenciarIdeiaServlet (RETENCAO-ACESSO) -- exige vinculo com a ideia pra quem nao e admin (IDOR corrigido).
public class GerenciarIdeiaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Gerenciamento", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String ideiaId, Map<String, Object> sessionAttrs) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(anyString())).thenAnswer(inv -> sessionAttrs.get(inv.getArgument(0, String.class)));
		doAnswer(inv -> sessionAttrs.put(inv.getArgument(0), inv.getArgument(1)))
				.when(session).setAttribute(anyString(), any());
		sessionAttrs.put("codigoUsuario", codigoUsuario);
		when(request.getParameter("ideiaId")).thenReturn(ideiaId);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void usuarioInexistente_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(999999L, "1", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void ideiaIdInvalido_redirecionaParaListaGerenciamento() throws Exception {
		Usuario u = novoUsuario("Solo", "usr");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-ideia-gerenciamento.jsp"));
	}

	@Test
	public void usuarioComumSemVinculo_bloqueadoRedirecionaParaLista() throws Exception {
		Usuario autor = novoUsuario("Autor", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario estranho = novoUsuario("Estranho", "usr");

		HttpServletRequest request = mockRequest(estranho.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-ideia-gerenciamento.jsp"));
	}

	@Test
	public void usuarioComVinculo_acessaGerenciamentoEArmazenaIdeiaIdNaSessao() throws Exception {
		Usuario autor = novoUsuario("Autor2", "usr");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("gerenciamento-ideia.jsp"));
		assertEquals(ideia.getCodigo(), attrs.get("ideiaId"));
	}

	@Test
	public void admin_acessaMesmoSemVinculo() throws Exception {
		Usuario autor = novoUsuario("Autor3", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario admin = novoUsuario("Admin", "adm");

		HttpServletRequest request = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new GerenciarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("gerenciamento-ideia.jsp"));
	}
}
