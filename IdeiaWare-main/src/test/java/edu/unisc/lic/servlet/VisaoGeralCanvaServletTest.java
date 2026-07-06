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
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 3: VisaoGeralCanvaServlet (CAN-09) -- confia no ideiaId ja validado na
 * sessao (nao repete o check de participacao, que e feito antes pelo EntrarCanvaServlet);
 * so protege contra ideiaId ausente/inexistente.
 */
public class VisaoGeralCanvaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaDAO canvaDAO = new CanvaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Visao Geral", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Object ideiaIdSessao, Map<String, Object> sessionAttrs) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("ideiaId")).thenReturn(ideiaIdSessao);
		doAnswer(inv -> sessionAttrs.put(inv.getArgument(0), inv.getArgument(1)))
				.when(session).setAttribute(anyString(), any());
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semIdeiaIdNaSessao_redirecionaListaCanvas() throws Exception {
		HttpServletRequest request = mockRequest(null, new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new VisaoGeralCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	public void ideiaIdSessaoInexistente_redirecionaListaCanvas() throws Exception {
		HttpServletRequest request = mockRequest(999999L, new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new VisaoGeralCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-canvas.jsp"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void ideiaValida_carregaListasERedirecionaParaVisaoGeral() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		canvaDAO.salvar(new Canva(ideia, "Post-it segmento", "ffeb3b", "segmento"));
		canvaDAO.salvar(new Canva(ideia, "Post-it parceria", "ffeb3b", "parceria"));

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(ideia.getCodigo(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new VisaoGeralCanvaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("canva-visao-geral.jsp"));

		List<Canva> segmento = (List<Canva>) attrs.get("segmento");
		List<Canva> atividade = (List<Canva>) attrs.get("atividade");
		assertEquals(1, segmento.size());
		assertEquals(0, atividade.size());
	}
}
