package edu.unisc.lic.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 1: SalvarTextoServlet -- autor sempre vem da sessao (nao mais parametro), so o LIDER edita o texto oficial.
public class SalvarTextoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia SalvarTexto", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String ideiaId, String texto) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("ideiaId")).thenReturn(ideiaId);
		when(request.getParameter("texto")).thenReturn(texto);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", "<p>texto</p>");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarTextoServlet().doPost(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("login.jsp"));
	}

	@Test
	public void ideiaInvalida_redirecionaParaColaboracao() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc", "<p>texto</p>");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarTextoServlet().doPost(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("colaboracao.jsp"));
	}

	@Test
	public void usuarioNaoLider_redirecionaMinhaIdeiaSemSalvar() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Usuario naoLider = novoUsuario("NaoLider");

		HttpServletRequest request = mockRequest(naoLider.getCodigo(), ideia.getCodigo().toString(), "<p>tentativa indevida</p>");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarTextoServlet().doPost(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("minha-ideia.jsp"));

		LogColaboracao filtro = new LogColaboracao();
		filtro.setIdeia(ideia);
		org.junit.Assert.assertTrue("nao pode ter salvo nada",
				logColaboracaoDAO.listarParametro(filtro).isEmpty());
	}

	@Test
	public void lider_salvaTextoComAutorDaSessaoNaoDoParametro() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo().toString(), "<p>Texto oficial</p>");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new SalvarTextoServlet().doPost(request, response);

		verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("colaboracao.jsp"));

		LogColaboracao filtro = new LogColaboracao();
		filtro.setIdeia(ideia);
		java.util.List<LogColaboracao> salvos = logColaboracaoDAO.listarParametro(filtro);
		org.junit.Assert.assertEquals(1, salvos.size());
		org.junit.Assert.assertEquals(autor.getCodigo(), salvos.get(0).getUsuario().getCodigo());
		org.junit.Assert.assertEquals("Texto oficial", salvos.get(0).getDescricao());
	}
}
