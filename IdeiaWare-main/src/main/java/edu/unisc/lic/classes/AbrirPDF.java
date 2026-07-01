package edu.unisc.lic.classes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

/**
 * Abre o PDF exportado (persona / point of view) no navegador.
 *
 * RKM-FIX (2026-06-17): antes fazia new File(caminho) esperando um CAMINHO de
 * arquivo no disco — mas o que e salvo em export_file.file_location e o conteudo
 * em base64 (data-URI) gerado pelo export do Toolkit (FileReader.readAsDataURL).
 * Logo new File("data:application/pdf;base64,...") apontava p/ um arquivo
 * inexistente e o "abrir PDF" estava QUEBRADO. Agora decodifica o base64 e
 * escreve os bytes do PDF na resposta.
 */
public class AbrirPDF {

	public static void abrir(HttpServletResponse response, String conteudo, String titulo) throws IOException {
		if (conteudo == null || conteudo.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Aceita tanto data-URI ("data:application/pdf;base64,XXXX") quanto base64 puro.
		String base64 = conteudo.trim();
		int virgula = base64.indexOf(',');
		if (base64.startsWith("data:") && virgula >= 0) {
			base64 = base64.substring(virgula + 1);
		}

		byte[] pdf;
		try {
			pdf = Base64.getDecoder().decode(base64);
		} catch (IllegalArgumentException e) {
			// conteudo nao e base64 valido (dado legado/corrompido) -> evita 500 cru
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=\"" + titulo + ".pdf\"");
		response.setContentLength(pdf.length);

		try (OutputStream out = response.getOutputStream()) {
			out.write(pdf);
			out.flush();
		}
	}
}
