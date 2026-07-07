package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
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
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

/**
 * M.2/M.3 (2026-07-06): RejeitarMembroServlet -- so o LIDER rejeita a entrada (P -> R) com
 * motivo; so vinculos pendentes. Regras checadas no servidor.
 */
public class RejeitarMembroServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia", "desc", StatusIdeia.VALIDADA, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private IdeiaUsuario vincula(Usuario u, Ideia ideia, String flLider, String status) {
		IdeiaUsuario iu = new IdeiaUsuario(u, ideia, flLider);
		iu.setFlStatusVinculo(status);
		iu.setDtInscricao();
		ideiaUsuarioDAO.salvar(iu);
		return iu;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String vinculo, String motivo) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("vinculo")).thenReturn(vinculo);
		when(request.getParameter("motivo")).thenReturn(motivo);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void liderRejeitaPendente_viraRejeitadoComMotivo() throws Exception {
		Usuario lider = novoUsuario("Lider");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S", StatusIdeia.VINCULO_APROVADO);
		Usuario candidato = novoUsuario("Candidato");
		IdeiaUsuario pend = vincula(candidato, ideia, "N", StatusIdeia.VINCULO_PENDENTE);

		HttpServletRequest request = mockRequest(lider.getCodigo(), pend.getCodigo().toString(), "Fora do perfil do grupo");
		new RejeitarMembroServlet().doPost(request, mock(HttpServletResponse.class));

		IdeiaUsuario recarregado = ideiaUsuarioDAO.buscar(pend.getCodigo());
		assertEquals(StatusIdeia.VINCULO_REJEITADO, recarregado.getFlStatusVinculo());
		assertEquals("Fora do perfil do grupo", recarregado.getMotivoRejeicaoMembro());
	}

	@Test
	public void naoLider_naoRejeita() throws Exception {
		Usuario lider = novoUsuario("Lider2");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S", StatusIdeia.VINCULO_APROVADO);
		Usuario candidato = novoUsuario("Candidato2");
		IdeiaUsuario pend = vincula(candidato, ideia, "N", StatusIdeia.VINCULO_PENDENTE);
		Usuario intruso = novoUsuario("Intruso");

		HttpServletRequest request = mockRequest(intruso.getCodigo(), pend.getCodigo().toString(), "hack");
		new RejeitarMembroServlet().doPost(request, mock(HttpServletResponse.class));

		assertEquals("nao-lider nao pode rejeitar", StatusIdeia.VINCULO_PENDENTE,
				ideiaUsuarioDAO.buscar(pend.getCodigo()).getFlStatusVinculo());
	}

	@Test
	public void motivoVazio_naoRejeitaEMantemPendente() throws Exception {
		// REVISAO 2026-07-07: motivo era exigido so no client (onsubmit do modal); um POST
		// forjado sem motivo gravava motivoRejeicaoMembro vazio. Agora e exigido no servidor.
		Usuario lider = novoUsuario("Lider3");
		Ideia ideia = novaIdeia(lider);
		vincula(lider, ideia, "S", StatusIdeia.VINCULO_APROVADO);
		Usuario candidato = novoUsuario("Candidato3");
		IdeiaUsuario pend = vincula(candidato, ideia, "N", StatusIdeia.VINCULO_PENDENTE);

		HttpServletRequest request = mockRequest(lider.getCodigo(), pend.getCodigo().toString(), "   ");
		new RejeitarMembroServlet().doPost(request, mock(HttpServletResponse.class));

		IdeiaUsuario recarregado = ideiaUsuarioDAO.buscar(pend.getCodigo());
		assertEquals("motivo vazio nao deve rejeitar", StatusIdeia.VINCULO_PENDENTE, recarregado.getFlStatusVinculo());
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", "x");
		HttpServletResponse response = mock(HttpServletResponse.class);
		new RejeitarMembroServlet().doPost(request, response);
		verify(response).sendRedirect(contains("login.jsp"));
	}
}
