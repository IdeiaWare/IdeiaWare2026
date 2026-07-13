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
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 2: ValidarIdeiaServlet (COL-07) -- so gestor (adm) valida/rejeita ideias.
public class ValidarIdeiaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeiaPendente(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Pendente", "desc", StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuarioLogado, String codigo, String codUsuario, String validar) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuarioLogado == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuarioLogado);
		}
		when(request.getParameter("codigo")).thenReturn(codigo);
		when(request.getParameter("codUsuario")).thenReturn(codUsuario);
		when(request.getParameter("validar")).thenReturn(validar);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", "1", "validar");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void usuarioNaoAdmin_redirecionaParaIndex() throws Exception {
		Usuario naoAdmin = novoUsuario("NaoAdmin", "usr");
		HttpServletRequest request = mockRequest(naoAdmin.getCodigo(), "1", naoAdmin.getCodigo().toString(), "validar");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("index.jsp"));
	}

	@Test
	public void admin_validaIdeia_mudaStatusParaValidada() throws Exception {
		Usuario admin = novoUsuario("Admin", "adm");
		Usuario autor = novoUsuario("Autor", "usr");
		Ideia ideia = novaIdeiaPendente(autor);

		HttpServletRequest request = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), admin.getCodigo().toString(), "validar");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doPost(request, response);

		Ideia recarregada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals(StatusIdeia.VALIDADA, recarregada.getStatus());
		verify(response).sendRedirect(contains("validar-ideia.jsp"));
	}

	@Test
	public void admin_rejeitaIdeia_mudaStatusParaRejeitadaComMotivo() throws Exception {
		Usuario admin = novoUsuario("Admin2", "adm");
		Usuario autor = novoUsuario("Autor2", "usr");
		Ideia ideia = novaIdeiaPendente(autor);

		HttpServletRequest request = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), admin.getCodigo().toString(), "rejeitar");
		when(request.getParameter("motivo")).thenReturn("Fora do escopo");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doPost(request, response);

		Ideia recarregada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals(StatusIdeia.REJEITADA, recarregada.getStatus());
		assertEquals("Fora do escopo", recarregada.getMotivoRejeicao());
	}

	@Test
	public void admin_reabreIdeiaRejeitada_voltaParaValidadaELimpaMotivo() throws Exception {
		// M.1: reabrir uma ideia REJEITADA volta pra VALIDADA (grupo aberto) e limpa o motivo de rejeicao.
		Usuario admin = novoUsuario("AdminReab", "adm");
		Usuario autor = novoUsuario("AutorReab", "usr");
		Ideia ideia = novaIdeiaPendente(autor);
		// primeiro rejeita
		HttpServletRequest reqRej = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), admin.getCodigo().toString(), "rejeitar");
		when(reqRej.getParameter("motivo")).thenReturn("Motivo qualquer");
		new ValidarIdeiaServlet().doPost(reqRej, mock(HttpServletResponse.class));
		assertEquals(StatusIdeia.REJEITADA, ideiaDAO.buscar(ideia.getCodigo()).getStatus());

		// agora reabre
		HttpServletRequest reqReab = mockRequest(admin.getCodigo(), ideia.getCodigo().toString(), admin.getCodigo().toString(), "reabrir");
		new ValidarIdeiaServlet().doPost(reqReab, mock(HttpServletResponse.class));

		Ideia recarregada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals(StatusIdeia.VALIDADA, recarregada.getStatus());
		assertEquals(StatusIdeia.GRUPO_ABERTO, recarregada.getStatusGrupo());
		org.junit.Assert.assertNull("motivo deve ser limpo ao reabrir", recarregada.getMotivoRejeicao());
	}

	@Test
	public void codigoInvalido_redirecionaParaValidarIdeiaSemQuebrar() throws Exception {
		Usuario admin = novoUsuario("Admin3", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "abc", admin.getCodigo().toString(), "validar");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("validar-ideia.jsp"));
	}

	@Test
	public void get_bloqueadoRedirecionaParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ValidarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}
}
