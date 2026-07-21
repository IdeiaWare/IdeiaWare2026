package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

// TEST-04, Tier 1: EntrarColaboracaoServlet -- cobre o bypass de admin (retencao) e o bloqueio de quem nao participa.
public class EntrarColaboracaoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Colab Servlet", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
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
		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(null, "1", attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("login.jsp"));
	}

	@Test
	public void ideiaIdInvalido_redirecionaParaMinhaIdeia() throws Exception {
		Usuario u = novoUsuario("Solo", "usr");
		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(u.getCodigo(), "nao-e-numero", attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void usuarioLogadoSemVinculoNaIdeia_naoAdminBloqueado() throws Exception {
		Usuario autor = novoUsuario("Autor", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario estranho = novoUsuario("Estranho", "usr"); // logado, mas NUNCA participou desta ideia

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(estranho.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void participanteDaIdeia_entraNaColaboracao() throws Exception {
		Usuario autor = novoUsuario("Autor2", "usr");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("colaboracao.jsp"));
		assertEquals(ideia.getCodigo(), attrs.get("ideiaId"));
		assertEquals("S", attrs.get("lider"));
	}

	@Test
	public void participantePendente_naoEntraNaColaboracao() throws Exception {
		// GT-02: checagem so testava iu==null, nao o status do vinculo (PENDENTE furava a lista de espera).
		Usuario autor = novoUsuario("AutorPend", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario candidato = novoUsuario("CandidatoPend", "usr");
		IdeiaUsuario vinculo = new IdeiaUsuario(candidato, ideia, "N");
		vinculo.setFlStatusVinculo(StatusIdeia.VINCULO_PENDENTE);
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(candidato.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void participanteRejeitado_naoEntraNaColaboracao() throws Exception {
		Usuario autor = novoUsuario("AutorRej", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario candidato = novoUsuario("CandidatoRej", "usr");
		IdeiaUsuario vinculo = new IdeiaUsuario(candidato, ideia, "N");
		vinculo.setFlStatusVinculo(StatusIdeia.VINCULO_REJEITADO);
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(candidato.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));
	}

	@Test
	public void participanteAprovado_entraNaColaboracao() throws Exception {
		Usuario autor = novoUsuario("AutorApr", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario candidato = novoUsuario("CandidatoApr", "usr");
		IdeiaUsuario vinculo = new IdeiaUsuario(candidato, ideia, "N");
		vinculo.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(candidato.getCodigo(), ideia.getCodigo().toString(), attrs);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("colaboracao.jsp"));
	}

	@Test
	public void adminSemVinculo_entraViaRetencao() throws Exception {
		Usuario autor = novoUsuario("Autor3", "usr");
		Ideia ideia = novaIdeia(autor);
		Usuario admin = novoUsuario("Admin", "adm"); // sem nenhum IdeiaUsuario nesta ideia

		Map<String, Object> attrs = new HashMap<>();
		HttpServletRequest request = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), attrs);
		when(request.getParameter("retencao")).thenReturn("true");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarColaboracaoServlet().doGet(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("colaboracao.jsp"));
		// ISRETENCAO-TIPO: sempre Boolean agora (antes guardava a String crua do parametro).
		assertEquals(Boolean.TRUE, attrs.get("isRetencao"));
	}
}
