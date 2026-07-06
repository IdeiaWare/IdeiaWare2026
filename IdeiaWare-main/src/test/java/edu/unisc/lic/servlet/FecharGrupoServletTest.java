package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
 * TEST-04, Tier 3 (2026-07-05): FecharGrupoServlet. Nota (K.8): a acao inteira roda em
 * varias transacoes separadas sem uma unica transacao guarda-chuva (achado K.8 #1,
 * ainda nao corrigido) -- estes testes cobrem o caminho sequencial feliz (sem falha no
 * meio), que ja funciona hoje.
 */
public class FecharGrupoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia", "desc", StatusIdeia.GRUPO_ABERTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private IdeiaUsuario vincula(Usuario u, Ideia ideia, String flLider) {
		IdeiaUsuario iu = new IdeiaUsuario(u, ideia, flLider);
		iu.setDtInscricao();
		ideiaUsuarioDAO.salvar(iu);
		return iu;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String codigoIdeia, String radio) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("codigo")).thenReturn(codigoIdeia);
		when(request.getParameter("radio")).thenReturn(radio);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FecharGrupoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void naoLider_naoFechaEredirecionaParaMinhaIdeia() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		vincula(autor, ideia, "S");
		Usuario naoLider = novoUsuario("NaoLider");

		HttpServletRequest request = mockRequest(naoLider.getCodigo(), ideia.getCodigo().toString(), null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FecharGrupoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
		Ideia recarregada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals("status do grupo nao deve mudar", StatusIdeia.GRUPO_ABERTO, recarregada.getStatusGrupo());
	}

	@Test
	public void lider_fechaGrupoSemTrocarLideranca() throws Exception {
		Usuario lider = novoUsuario("Lider");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S");

		HttpServletRequest request = mockRequest(lider.getCodigo(), ideia.getCodigo().toString(), null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FecharGrupoServlet().doPost(request, response);

		Ideia recarregada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals(StatusIdeia.GRUPO_FECHADO, recarregada.getStatusGrupo());
		assertEquals(StatusIdeia.EM_DESENVOLVIMENTO, recarregada.getStatus());
		verify(response).sendRedirect(contains("minha-ideia.jsp"));
	}

	@Test
	public void lider_transfereLiderancaViaRadio() throws Exception {
		Usuario lider = novoUsuario("Lider2");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S");
		Usuario novoLider = novoUsuario("NovoLider");
		vincula(novoLider, ideia, "N");

		HttpServletRequest request = mockRequest(lider.getCodigo(), ideia.getCodigo().toString(), novoLider.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FecharGrupoServlet().doPost(request, response);

		List<IdeiaUsuario> vinculoNovoLider = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(novoLider, ideia, null));
		assertEquals("S", vinculoNovoLider.get(0).getFlLider());

		List<IdeiaUsuario> vinculoAntigoLider = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(lider, ideia, null));
		assertEquals("N", vinculoAntigoLider.get(0).getFlLider());
	}
}
