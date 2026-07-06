package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * TEST-04: reativado/criado a partir da varredura em classes/ (2026-07-03). titulo=null
 * era o caso real (ExportFile.fileName nunca e preenchido em nenhum dos 2 projetos --
 * confirmado por grep) que fazia todo PDF de Persona/POV baixar como "null.pdf".
 */
public class AbrirPDFTest {

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

	@Test
	public void abrir_conteudoNulo_retorna404SemEscrever() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);

		AbrirPDF.abrir(response, null, "titulo");

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void abrir_base64Invalido_retorna500() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);

		AbrirPDF.abrir(response, "isso nao e base64 valido!!!", "titulo");

		verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Test
	public void abrir_tituloNulo_usaNomeDeFallbackEmVezDeNull() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String conteudo = Base64.getEncoder().encodeToString("PDF-fake".getBytes());

		AbrirPDF.abrir(response, conteudo, null);

		ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
		verify(response).setHeader(eq("Content-disposition"), header.capture());
		assertEquals("inline; filename=\"documento.pdf\"", header.getValue());
	}

	@Test
	public void abrir_tituloComAspasEQuebraDeLinha_removeAntesDeIrProHeader() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String conteudo = Base64.getEncoder().encodeToString("PDF-fake".getBytes());

		AbrirPDF.abrir(response, conteudo, "Persona \"Maligna\"\r\nX-Injected: 1");

		ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
		verify(response).setHeader(eq("Content-disposition"), header.capture());
		assertEquals("inline; filename=\"Persona MalignaX-Injected: 1.pdf\"", header.getValue());
	}

	@Test
	public void abrir_dataUri_decodificaSoAParteBase64EEscreveOsBytes() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(fakeOutputStream(sink));
		String base64 = Base64.getEncoder().encodeToString("conteudo-do-pdf".getBytes());

		AbrirPDF.abrir(response, "data:application/pdf;base64," + base64, "ok");

		assertEquals("conteudo-do-pdf", sink.toString());
	}
}
