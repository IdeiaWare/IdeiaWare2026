package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: ExportaStoryServlet -- blindagem contra sessao sem storytellingId, id nao-numerico e corpo vazio.
public class ExportaStoryServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	private static final String PDF_BASE64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes(StandardCharsets.UTF_8));

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Storytelling novoStorytelling() {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = new Ideia(autor, "Ideia Story", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "DE");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequest(Object storytellingId, String body) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body == null ? "" : body)));
		return request;
	}

	@Test
	public void corpoVazio_retorna400() throws Exception {
		Storytelling st = novoStorytelling();
		HttpServletRequest request = mockRequest(st.getCodigo(), "   ");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void semStorytellingIdNaSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void storytellingIdNaoNumerico_retorna400() throws Exception {
		HttpServletRequest request = mockRequest("abc", "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void storytellingInexistente_retorna404() throws Exception {
		HttpServletRequest request = mockRequest(999999L, "base64PdfFake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void dadosValidos_finalizaStorytellingEAvancaIdeiaParaCaixaFerramentas() throws Exception {
		Storytelling st = novoStorytelling();
		HttpServletRequest request = mockRequest(st.getCodigo(), PDF_BASE64);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportaStoryServlet().doPost(request, response);

		Storytelling atualizado = storytellingDAO.buscar(st.getCodigo());
		assertEquals(StatusIdeia.FINALIZADO, atualizado.getStatus());
		String caminhoEsperado = Constantes.CAMINHO_EXPORT_STORYTELLING + st.getIdeia().getCodigo() + ".pdf";
		assertEquals(caminhoEsperado, atualizado.getCaminhoFinalizado());
		byte[] gravado = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoEsperado).toPath());
		assertEquals("conteudo-pdf-fake", new String(gravado, StandardCharsets.UTF_8));

		Ideia ideiaAtualizada = ideiaDAO.buscar(st.getIdeia().getCodigo());
		assertEquals(StatusIdeia.CAIXA_FERRAMENTAS, ideiaAtualizada.getStatus());
	}

	@Test
	public void storytellingJaFinalizado_bloqueiaReExportacao() throws Exception { // UX-STORY-EXPORT-DUPLO
		// simula o cenario real: 1o usuario exporta com sucesso...
		Storytelling st = novoStorytelling();
		HttpServletRequest request1 = mockRequest(st.getCodigo(), PDF_BASE64);
		new ExportaStoryServlet().doPost(request1, mock(HttpServletResponse.class));
		String caminhoOriginal = storytellingDAO.buscar(st.getCodigo()).getCaminhoFinalizado();

		// ...uma 2a aba (outro participante), com o storytellingId ainda na sessao, tenta
		// exportar de novo com um PDF DIFERENTE -- antes do fix, isso sobrescrevia o arquivo.
		String pdfSegundaVersao = Base64.getEncoder().encodeToString("outro-conteudo".getBytes(StandardCharsets.UTF_8));
		HttpServletRequest request2 = mockRequest(st.getCodigo(), pdfSegundaVersao);
		HttpServletResponse response2 = mock(HttpServletResponse.class);
		when(response2.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

		new ExportaStoryServlet().doPost(request2, response2);

		verify(response2).setStatus(HttpServletResponse.SC_FORBIDDEN);
		Storytelling aposSegundaTentativa = storytellingDAO.buscar(st.getCodigo());
		assertEquals("caminho do 1o export nao deve ter sido sobrescrito", caminhoOriginal, aposSegundaTentativa.getCaminhoFinalizado());
		byte[] gravado = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoOriginal).toPath());
		assertEquals("conteudo do PDF deve continuar sendo o da 1a exportacao",
				"conteudo-pdf-fake", new String(gravado, StandardCharsets.UTF_8));
	}
}
