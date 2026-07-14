package edu.unisc.lic.servlet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import edu.unisc.lic.classes.ArquivoExport;
import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ExportFileDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: DeletarExportedFileServlet (RKM-02) -- so admin exclui, id inexistente nao levanta excecao.
public class DeletarExportedFileServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final ExportFileDAO exportFileDAO = new ExportFileDAO();

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private ExportFile novoExportFile() throws Exception {
		Usuario autor = novoUsuario("Dono", "usr");
		Ideia ideia = new Ideia(autor, "Ideia Export", "desc", StatusIdeia.CAIXA_FERRAMENTAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		String base64 = Base64.getEncoder().encodeToString("x".getBytes(StandardCharsets.UTF_8));
		String caminhoRelativo = ArquivoExport.salvar(base64, "conhecimento" + File.separator + System.nanoTime() + ".pdf");
		ExportFile ef = new ExportFile(ideia, caminhoRelativo);
		ef.setFileTypeIdentification("persona");
		exportFileDAO.salvar(ef);
		return ef;
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
	public void semSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void usuarioNaoAdmin_retorna403() throws Exception {
		Usuario u = novoUsuario("Comum", "usr");
		HttpServletRequest request = mockRequest(u.getCodigo(), "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void corpoVazio_retorna400() throws Exception {
		Usuario admin = novoUsuario("Admin", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void corpoNaoNumerico_retorna400() throws Exception {
		Usuario admin = novoUsuario("Admin2", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void idInexistente_naoLancaExcecao() throws Exception {
		Usuario admin = novoUsuario("Admin3", "adm");
		HttpServletRequest request = mockRequest(admin.getCodigo(), "999999999");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void adminValido_excluiArquivoExportado() throws Exception {
		Usuario admin = novoUsuario("Admin4", "adm");
		ExportFile export = novoExportFile();
		String caminhoRelativo = export.getFileLocation();

		HttpServletRequest request = mockRequest(admin.getCodigo(), export.getId().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new DeletarExportedFileServlet().doPost(request, response);

		assertNull(exportFileDAO.buscar(export.getId()));
		assertFalse("arquivo em disco tambem deve ter sido excluido",
				new File(Constantes.caminhoExports() + caminhoRelativo).exists());
	}
}
