package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: UploadArquivoServlet (STR-06) -- allowlist de extensao de imagem impede RCE via upload; testes montam o corpo multipart na mao.
public class UploadArquivoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Storytelling novoStorytelling() {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = new Ideia(autor, "Ideia Upload", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "DE");
		storytellingDAO.salvar(st);
		return st;
	}

	// getRealPath("") pra gravar na pasta de imagens -- precisa de init(ServletConfig) fora de container real, senao NPE.
	private UploadArquivoServlet novoServletComContexto() throws Exception {
		UploadArquivoServlet servlet = new UploadArquivoServlet();
		ServletConfig config = mock(ServletConfig.class);
		ServletContext context = mock(ServletContext.class);
		when(config.getServletContext()).thenReturn(context);
		when(context.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));
		servlet.init(config);
		return servlet;
	}

	private byte[] pngValido() throws Exception {
		BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", bos);
		return bos.toByteArray();
	}

	private byte[] construirCorpoMultipart(String boundary, String nomeArquivo, String contentType, byte[] conteudo) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String cabecalho = "--" + boundary + "\r\n"
				+ "Content-Disposition: form-data; name=\"arquivo\"; filename=\"" + nomeArquivo + "\"\r\n"
				+ "Content-Type: " + contentType + "\r\n\r\n";
		bos.write(cabecalho.getBytes(StandardCharsets.ISO_8859_1));
		bos.write(conteudo);
		bos.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.ISO_8859_1));
		return bos.toByteArray();
	}

	private HttpServletRequest mockRequestMultipart(Long storytellingId, String boundary, byte[] corpo) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		// ServletFileUpload.isMultipartContent() exige method "post" ALEM do Content-Type (senao volta false em silencio).
		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn("multipart/form-data; boundary=" + boundary);
		when(request.getContentLength()).thenReturn(corpo.length);
		when(request.getInputStream()).thenReturn(new ServletInputStream() {
			private final ByteArrayInputStream delegate = new ByteArrayInputStream(corpo);

			@Override
			public int read() {
				return delegate.read();
			}

			@Override
			public boolean isFinished() {
				return delegate.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}
		});
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semSessao_retorna401() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession(false)).thenReturn(null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new UploadArquivoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void semStorytellingIdNaSessao_retorna401() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new UploadArquivoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void storytellingInexistente_retorna404() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(999999L);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new UploadArquivoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void naoMultipart_naoRedirecionaNemFalha() throws Exception {
		Storytelling st = novoStorytelling();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(st.getCodigo());
		when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
		HttpServletResponse response = mock(HttpServletResponse.class);

		novoServletComContexto().doPost(request, response);

		verify(response, never()).sendRedirect(anyString());
		verify(response, never()).setStatus(anyInt());
	}

	@Test
	public void extensaoNaoPermitida_ignoraArquivoERedirecionaSemSalvarElemento() throws Exception {
		Storytelling st = novoStorytelling();
		String boundary = "----TestBoundary" + System.nanoTime();
		byte[] corpo = construirCorpoMultipart(boundary, "malicioso.txt", "text/plain",
				"conteudo qualquer".getBytes(StandardCharsets.ISO_8859_1));

		HttpServletRequest request = mockRequestMultipart(st.getCodigo(), boundary, corpo);
		HttpServletResponse response = mock(HttpServletResponse.class);

		novoServletComContexto().doPost(request, response);

		verify(response).sendRedirect(contains("storytelling.jsp"));

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		assertEquals(0, elementosStorytellingDAO.listarParametro(filtro).size());
	}

	@Test
	public void uploadImagemPngValida_salvaElementoERedireciona() throws Exception {
		Storytelling st = novoStorytelling();
		byte[] png = pngValido();
		String boundary = "----TestBoundary" + System.nanoTime();
		byte[] corpo = construirCorpoMultipart(boundary, "imagem.png", "image/png", png);

		HttpServletRequest request = mockRequestMultipart(st.getCodigo(), boundary, corpo);
		HttpServletResponse response = mock(HttpServletResponse.class);

		novoServletComContexto().doPost(request, response);

		verify(response).sendRedirect(contains("storytelling.jsp"));

		ElementosStorytelling filtro = new ElementosStorytelling();
		filtro.setStorytelling(st);
		List<ElementosStorytelling> salvos = elementosStorytellingDAO.listarParametro(filtro);
		assertEquals(1, salvos.size());
		assertEquals("IMG", salvos.get(0).getTipo());
	}
}
