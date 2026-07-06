package edu.unisc.lic.servlet;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaexportDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 3: DeletarCanvaexportServlet (RKM-02) -- exclui um Canvaexport a partir
 * da Retencao do Conhecimento; admin-only, espelhando o DeletarExportedFileServlet.
 */
public class DeletarCanvaexportServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaexportDAO canvaexportDAO = new CanvaexportDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Canvaexport", "desc", StatusIdeia.FINALIZADO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String body) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body == null ? "" : body)));
		return request;
	}

	@Test
	public void semLogin_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void usuarioNaoAdmin_retorna403() throws Exception {
		Usuario comum = novoUsuario("Comum", "usr");
		HttpServletRequest request = mockRequest(comum.getCodigo(), "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void corpoVazio_retorna400() throws Exception {
		Usuario admin = novoUsuario("Admin", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void corpoNaoNumerico_retorna400() throws Exception {
		Usuario admin = novoUsuario("Admin2", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void codigoInexistente_naoQuebraNemDefineStatusDeErro() throws Exception {
		Usuario admin = novoUsuario("Admin3", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "999999");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		verify(response, never()).setStatus(anyInt());
	}

	@Test
	public void admin_excluiCanvaexport() throws Exception {
		Usuario admin = novoUsuario("Admin4", "adm");
		Usuario autor = novoUsuario("Autor", "usr");
		Ideia ideia = novaIdeia(autor);
		Canvaexport export = new Canvaexport(ideia, "conteudo-canva-export", Data.horaAtual());
		canvaexportDAO.salvar(export);

		HttpServletRequest request = mockRequest(admin.getCodigo(), export.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarCanvaexportServlet().doPost(request, response);

		assertNull("canvaexport deve ter sido excluido", canvaexportDAO.buscar(export.getCodigo()));
	}
}
