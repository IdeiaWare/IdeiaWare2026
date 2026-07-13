package edu.unisc.lic.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ExportFileDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 1: AbrirPersona exigia login mas nao checava dono do PDF (IDOR); admin mantem bypass.
public class AbrirPersonaTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final ExportFileDAO exportFileDAO = new ExportFileDAO();

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Persona", "desc", StatusIdeia.CAIXA_FERRAMENTAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private ExportFile novoExport(Ideia ideia) {
		String base64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes());
		ExportFile ef = new ExportFile(ideia, base64);
		ef.setFileTypeIdentification("persona");
		exportFileDAO.salvar(ef);
		return ef;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String codigoPersona) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("codigoPersona")).thenReturn(codigoPersona);
		return request;
	}

	private HttpServletResponse mockResponse() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
			@Override
			public void write(int b) {
				sink.write(b);
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setWriteListener(javax.servlet.WriteListener writeListener) {
			}
		});
		return response;
	}

	@Test
	public void semLogin_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "1");
		HttpServletResponse response = mockResponse();

		new AbrirPersona().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void exportInexistente_retorna404() throws Exception {
		Usuario u = novoUsuario("Solo", "usr");
		HttpServletRequest request = mockRequest(u.getCodigo(), "999999");
		HttpServletResponse response = mockResponse();

		new AbrirPersona().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void usuarioLogadoSemVinculoComAIdeia_retorna403() throws Exception {
		Usuario dono = novoUsuario("Dono", "usr");
		Ideia ideia = novaIdeia(dono);
		ExportFile export = novoExport(ideia);

		Usuario estranho = novoUsuario("Estranho", "usr"); // logado, nao participa desta ideia
		HttpServletRequest request = mockRequest(estranho.getCodigo(), export.getId().toString());
		HttpServletResponse response = mockResponse();

		new AbrirPersona().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void participanteDaIdeia_recebeOPdf() throws Exception {
		Usuario dono = novoUsuario("Dono2", "usr");
		Ideia ideia = novaIdeia(dono);
		ExportFile export = novoExport(ideia);
		IdeiaUsuario vinculo = new IdeiaUsuario(dono, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(dono.getCodigo(), export.getId().toString());
		HttpServletResponse response = mockResponse();

		new AbrirPersona().doGet(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_NOT_FOUND);
		verify(response, org.mockito.Mockito.atLeastOnce()).setContentType("application/pdf");
	}

	@Test
	public void admin_baixaMesmoSemParticipar() throws Exception {
		Usuario dono = novoUsuario("Dono3", "usr");
		Ideia ideia = novaIdeia(dono);
		ExportFile export = novoExport(ideia);
		Usuario admin = novoUsuario("Admin", "adm"); // sem vinculo nenhum

		HttpServletRequest request = mockRequest(admin.getCodigo(), export.getId().toString());
		HttpServletResponse response = mockResponse();

		new AbrirPersona().doGet(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response, org.mockito.Mockito.atLeastOnce()).setContentType("application/pdf");
	}
}
