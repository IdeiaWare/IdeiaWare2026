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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: EntrarCanvaServlet (CAN-09) -- ponto de entrada principal do Canvas, exige participacao e carrega os 9 blocos na sessao.
public class EntrarCanvaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final CanvaDAO canvaDAO = new CanvaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Canva", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String paramIdeiaId, Map<String, Object> sessionAttrs) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(anyString())).thenAnswer(inv -> sessionAttrs.get(inv.getArgument(0, String.class)));
		doAnswer(inv -> sessionAttrs.put(inv.getArgument(0), inv.getArgument(1)))
				.when(session).setAttribute(anyString(), any());
		if (codigoUsuario != null) {
			sessionAttrs.put("codigoUsuario", codigoUsuario);
		}
		when(request.getParameter("ideiaId")).thenReturn(paramIdeiaId);
		when(request.getParameter("retencao")).thenReturn(null);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semIdeiaIdNemParametroNemSessao_redirecionaListaCanvas() throws Exception {
		HttpServletRequest request = mockRequest(null, null, new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	public void ideiaParamInexistente_redirecionaListaCanvas() throws Exception {
		HttpServletRequest request = mockRequest(null, "999999", new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	public void semLogin_redirecionaParaLoginEmVezDeNPE() throws Exception {
		// TEST-04: faltava checagem de login -- unboxing de getAttribute("codigoUsuario") sem checar null dava NPE em vez de redirecionar.
		Usuario autor = novoUsuario("AutorSemLogin");
		Ideia ideia = novaIdeia(autor);

		HttpServletRequest request = mockRequest(null, ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void usuarioNaoParticipante_redirecionaListaCanvas() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Usuario estranho = novoUsuario("Estranho");

		HttpServletRequest request = mockRequest(estranho.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void participante_carregaBlocosDoCanvaERedirecionaParaMapa() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);
		canvaDAO.salvar(new Canva(ideia, "Post-it receita", "ffeb3b", "receita"));
		canvaDAO.salvar(new Canva(ideia, "Post-it custo", "ffeb3b", "custo"));

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("canva-mapa.jsp"));
		assertEquals(ideia.getCodigo(), attrs.get("ideiaId"));
		assertEquals("S", attrs.get("lider"));

		List<Canva> receita = (List<Canva>) attrs.get("receita");
		List<Canva> atividade = (List<Canva>) attrs.get("atividade");
		assertEquals(1, receita.size());
		assertEquals(0, atividade.size());
	}
}
