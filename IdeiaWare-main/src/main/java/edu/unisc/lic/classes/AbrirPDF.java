package edu.unisc.lic.classes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

// RKM-FIX: decodifica base64 e escreve os bytes do PDF (antes, esperava um caminho de arquivo em disco).
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

		// RET-14: titulo pode vir null (antes, PDF baixava como "null.pdf") ou com aspas/quebra de linha.
		String nomeArquivo = (titulo == null || titulo.trim().isEmpty()) ? "documento" : titulo.trim();
		nomeArquivo = nomeArquivo.replaceAll("[\\r\\n\"]", "");

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=\"" + nomeArquivo + ".pdf\"");
		response.setContentLength(pdf.length);

		try (OutputStream out = response.getOutputStream()) {
			out.write(pdf);
			out.flush();
		}
	}
}
