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

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 1: EntrarStorytellingServlet -- sem bypass de admin, so participante de verdade entra.
public class EntrarStorytellingServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Story Servlet", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
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

		new EntrarStorytellingServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("login.jsp"));
	}

	@Test
	public void usuarioSemVinculo_bloqueado() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Usuario estranho = novoUsuario("Estranho");

		HttpServletRequest request = mockRequest(estranho.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarStorytellingServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void participanteMasSemStorytellingCriado_redirecionaMinhaIdeia() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarStorytellingServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void participanteComStorytellingEmDesenvolvimento_vaiParaTelaDeEdicao() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Storytelling story = new Storytelling(autor, ideia, Data.horaAtual(), StatusIdeia.EM_DESENVOLVIMENTO);
		storytellingDAO.salvar(story);
		logColaboracaoDAO.salvar(new LogColaboracao(ideia, autor, Data.horaAtual(), "Descricao base"));

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarStorytellingServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("storytelling.jsp"));
		assertEquals(story.getCodigo(), attrs.get("storytellingId"));
	}

	@Test
	public void participanteComStorytellingFinalizado_vaiParaTelaDeVisualizacao() throws Exception {
		Usuario autor = novoUsuario("Autor4");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Storytelling story = new Storytelling(autor, ideia, Data.horaAtual(), StatusIdeia.FINALIZADO);
		storytellingDAO.salvar(story);
		logColaboracaoDAO.salvar(new LogColaboracao(ideia, autor, Data.horaAtual(), "Descricao base"));

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), new HashMap<>());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarStorytellingServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("storytelling-show.jsp"));
	}
}
