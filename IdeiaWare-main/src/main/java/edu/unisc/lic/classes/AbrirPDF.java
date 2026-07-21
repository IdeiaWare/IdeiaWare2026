package edu.unisc.lic.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

// PDF-DISCO
public class AbrirPDF {

	public static void abrir(HttpServletResponse response, String caminhoRelativo, String titulo) throws IOException {
		if (caminhoRelativo == null || caminhoRelativo.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		File arquivo = new File(Constantes.caminhoExports() + caminhoRelativo.trim());
		if (!arquivo.isFile()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// RET-14: titulo pode vir null ou com aspas/quebra de linha
		String nomeArquivo = (titulo == null || titulo.trim().isEmpty()) ? "documento" : titulo.trim();
		nomeArquivo = nomeArquivo.replaceAll("[\\r\\n\"]", "");

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=\"" + nomeArquivo + ".pdf\"");
		response.setContentLength((int) arquivo.length());

		try (FileInputStream in = new FileInputStream(arquivo); OutputStream out = response.getOutputStream()) {
			byte[] buffer = new byte[8192];
			int lidos;
			while ((lidos = in.read(buffer)) != -1) {
				out.write(buffer, 0, lidos);
			}
			out.flush();
		}
	}
}
