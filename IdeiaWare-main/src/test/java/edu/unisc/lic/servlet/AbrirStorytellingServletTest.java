package edu.unisc.lic.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import edu.unisc.lic.classes.ArquivoExport;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// AUDITORIA-2026-07-16: PDF-DISCO criou este servlet sem teste (irmaos AbrirPersona/AbrirPointOfView ja tinham).
public class AbrirStorytellingServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	private Usuario novoUsuario(String nome, String permissao) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", permissao, nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Storytelling", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private Storytelling novoStorytelling(Usuario lider, Ideia ideia) throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes());
		String caminhoRelativo = ArquivoExport.salvar(base64, "storytelling" + File.separator + System.nanoTime() + ".pdf");
		Storytelling st = new Storytelling(lider, ideia, null, StatusIdeia.FINALIZADO);
		st.setDtCriacao();
		st.setCaminhoFinalizado(caminhoRelativo);
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String id) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("id")).thenReturn(id);
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

		new AbrirStorytellingServlet().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void storytellingInexistente_retorna404() throws Exception {
		Usuario u = novoUsuario("Solo", "usr");
		HttpServletRequest request = mockRequest(u.getCodigo(), "999999");
		HttpServletResponse response = mockResponse();

		new AbrirStorytellingServlet().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void usuarioLogadoSemVinculoComAIdeia_retorna403() throws Exception {
		Usuario dono = novoUsuario("Dono", "usr");
		Ideia ideia = novaIdeia(dono);
		Storytelling st = novoStorytelling(dono, ideia);

		Usuario estranho = novoUsuario("Estranho", "usr"); // logado, nao participa desta ideia
		HttpServletRequest request = mockRequest(estranho.getCodigo(), st.getCodigo().toString());
		HttpServletResponse response = mockResponse();

		new AbrirStorytellingServlet().doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void participanteDaIdeia_recebeOPdf() throws Exception {
		Usuario dono = novoUsuario("Dono2", "usr");
		Ideia ideia = novaIdeia(dono);
		Storytelling st = novoStorytelling(dono, ideia);
		IdeiaUsuario vinculo = new IdeiaUsuario(dono, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(dono.getCodigo(), st.getCodigo().toString());
		HttpServletResponse response = mockResponse();

		new AbrirStorytellingServlet().doGet(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_NOT_FOUND);
		verify(response, org.mockito.Mockito.atLeastOnce()).setContentType("application/pdf");
	}

	@Test
	public void admin_baixaMesmoSemParticipar() throws Exception {
		Usuario dono = novoUsuario("Dono3", "usr");
		Ideia ideia = novaIdeia(dono);
		Storytelling st = novoStorytelling(dono, ideia);
		Usuario admin = novoUsuario("Admin", "adm"); // sem vinculo nenhum

		HttpServletRequest request = mockRequest(admin.getCodigo(), st.getCodigo().toString());
		HttpServletResponse response = mockResponse();

		new AbrirStorytellingServlet().doGet(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response, org.mockito.Mockito.atLeastOnce()).setContentType("application/pdf");
	}
}
