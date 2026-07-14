package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

// TEST-04: titulo=null era o caso real (ExportFile.fileName nunca preenchido) que fazia PDF baixar como "null.pdf".
// PDF-DISCO
public class AbrirPDFTest {

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	private ServletOutputStream fakeOutputStream(ByteArrayOutputStream sink) {
		return new ServletOutputStream() {
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
		};
	}

	private String escreverArquivoDeTeste(String conteudo) throws Exception {
		String caminhoRelativo = "abrir-pdf-test" + File.separator + System.nanoTime() + ".pdf";
		File arquivo = new File(Constantes.caminhoExports() + caminhoRelativo);
		arquivo.getParentFile().mkdirs();
		Files.write(arquivo.toPath(), conteudo.getBytes(StandardCharsets.UTF_8));
		return caminhoRelativo;
	}

	@Test
	public void abrir_conteudoNulo_retorna404SemEscrever() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);

		AbrirPDF.abrir(response, null, "titulo");

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void abrir_arquivoNaoExiste_retorna404() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);

		AbrirPDF.abrir(response, "nao-existe" + File.separator + "fantasma.pdf", "titulo");

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void abrir_tituloNulo_usaNomeDeFallbackEmVezDeNull() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String caminhoRelativo = escreverArquivoDeTeste("PDF-fake");

		AbrirPDF.abrir(response, caminhoRelativo, null);

		ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
		verify(response).setHeader(eq("Content-disposition"), header.capture());
		assertEquals("inline; filename=\"documento.pdf\"", header.getValue());
	}

	@Test
	public void abrir_tituloComAspasEQuebraDeLinha_removeAntesDeIrProHeader() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String caminhoRelativo = escreverArquivoDeTeste("PDF-fake");

		AbrirPDF.abrir(response, caminhoRelativo, "Persona \"Maligna\"\r\nX-Injected: 1");

		ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
		verify(response).setHeader(eq("Content-disposition"), header.capture());
		assertEquals("inline; filename=\"Persona MalignaX-Injected: 1.pdf\"", header.getValue());
	}

	@Test
	public void abrir_arquivoExiste_leEEscreveOsBytesExatos() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String caminhoRelativo = escreverArquivoDeTeste("conteudo-do-pdf");

		AbrirPDF.abrir(response, caminhoRelativo, "ok");

		assertEquals("conteudo-do-pdf", sink.toString());
	}
}
