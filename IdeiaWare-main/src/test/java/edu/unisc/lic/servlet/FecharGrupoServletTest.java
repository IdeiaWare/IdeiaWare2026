package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

// TEST-04, Tier 3/K.8 #1: FecharGrupoServlet -- acao roda em varias transacoes separadas (ainda nao corrigido); testes cobrem o caminho feliz sequencial.
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

	private IdeiaUsuario vincula(Usuario u, Ideia ideia, String flLider, String statusVinculo) {
		IdeiaUsuario iu = new IdeiaUsuario(u, ideia, flLider);
		iu.setFlStatusVinculo(statusVinculo);
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

	@Test
	public void fecharGrupo_removeVinculosPendentesERejeitados() throws Exception {
		// M.2: ao fechar, quem nao foi aprovado (P/R) e removido; sobram so os aprovados (A).
		Usuario lider = novoUsuario("LiderClean");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S", StatusIdeia.VINCULO_APROVADO);
		Usuario aprovado = novoUsuario("Aprovado");
		vincula(aprovado, ideia, "N", StatusIdeia.VINCULO_APROVADO);
		Usuario pendente = novoUsuario("Pendente");
		vincula(pendente, ideia, "N", StatusIdeia.VINCULO_PENDENTE);
		Usuario rejeitado = novoUsuario("Rejeitado");
		vincula(rejeitado, ideia, "N", StatusIdeia.VINCULO_REJEITADO);

		HttpServletRequest request = mockRequest(lider.getCodigo(), ideia.getCodigo().toString(), null);
		new FecharGrupoServlet().doPost(request, mock(HttpServletResponse.class));

		assertTrue("pendente deve ser removido ao fechar",
				ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(pendente, ideia, null)).isEmpty());
		assertTrue("rejeitado deve ser removido ao fechar",
				ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(rejeitado, ideia, null)).isEmpty());
		assertEquals("aprovado deve permanecer", 1,
				ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(aprovado, ideia, null)).size());
		assertEquals("lider deve permanecer", 1,
				ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(lider, ideia, null)).size());
	}

	@Test
	public void fecharGrupo_naoPromovePendenteALider() throws Exception {
		// M.2: POST forjado com id de PENDENTE nao pode torna-lo lider (servidor barra, nao so a UI).
		Usuario lider = novoUsuario("LiderP");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S", StatusIdeia.VINCULO_APROVADO);
		Usuario pendente = novoUsuario("PendenteP");
		vincula(pendente, ideia, "N", StatusIdeia.VINCULO_PENDENTE);

		HttpServletRequest request = mockRequest(lider.getCodigo(), ideia.getCodigo().toString(), pendente.getCodigo().toString());
		new FecharGrupoServlet().doPost(request, mock(HttpServletResponse.class));

		// o lider continua sendo o lider; o pendente foi removido (nao virou lider).
		List<IdeiaUsuario> vinculoLider = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(lider, ideia, "S"));
		assertEquals("o lider original deve continuar lider", 1, vinculoLider.size());
		assertTrue("pendente removido, nunca virou lider",
				ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(pendente, ideia, null)).isEmpty());
	}
}
