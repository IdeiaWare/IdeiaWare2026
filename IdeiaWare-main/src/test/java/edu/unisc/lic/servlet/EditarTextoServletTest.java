package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04 (2026-07-03), Tier 1: EditarTextoServlet (STR-15/SRV-IDOR-06) -- achado so
 * na SEGUNDA passada da varredura de servlets. Mesmo padrao do SalvarTextoServlet:
 * exige login + lideranca antes de abrir o editor.
 */
public class EditarTextoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor, String descricao) {
		Ideia ideia = new Ideia(autor, "Ideia EditarTexto", descricao, StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String ideiaId, Map<String, Object> sessionAttrs) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(anyString())).thenAnswer(inv -> sessionAttrs.get(inv.getArgument(0, String.class)));
		org.mockito.Mockito.doAnswer(inv -> sessionAttrs.put(inv.getArgument(0), inv.getArgument(1)))
				.when(session).setAttribute(anyString(), org.mockito.ArgumentMatchers.any());
		sessionAttrs.put("codigoUsuario", codigoUsuario);
		when(request.getParameter("ideiaId")).thenReturn(ideiaId);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EditarTextoServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void ideiaInvalida_redirecionaMinhaIdeia() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EditarTextoServlet().doGet(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
	}

	@Test
	public void usuarioNaoLider_bloqueadoNaoAbreEditor() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor, "Descricao original");
		Usuario naoLider = novoUsuario("NaoLider");

		HttpServletRequest request = mockRequest(naoLider.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EditarTextoServlet().doGet(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
	}

	@Test
	public void lider_semColaboracaoAinda_usaDescricaoDaIdeiaComoFallback() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor, "Descricao original da ideia");
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EditarTextoServlet().doGet(request, response);

		verify(response).sendRedirect(contains("editar-texto.jsp"));
		assertEquals("<p>Descricao original da ideia</p>", attrs.get("ideiaDescricao"));
	}

	@Test
	public void lider_comColaboracaoSalva_usaADescricaoFinalNaoAOriginal() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor, "Descricao original da ideia");
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);
		logColaboracaoDAO.salvar(new LogColaboracao(ideia, autor, Data.horaAtual(), "Texto oficial mais recente"));

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EditarTextoServlet().doGet(request, response);

		assertEquals("<p>Texto oficial mais recente</p>", attrs.get("ideiaDescricao"));
	}
}
