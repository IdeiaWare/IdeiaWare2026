package br.unisc.toolkit.classes;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

public class Constantes {

	public static final String CAMINHO_EXPORT_CONHECIMENTO = "conhecimento" + File.separator;

	public static String caminhoExports() {
		return System.getProperty("ideiaware.exports.dir",
				File.separator + "data" + File.separator + "ideiaware-exports") + File.separator;
	}

	// UX-PADRAO-ETAPA-FINALIZADA: pagina do LIC pro redirect apos aviso de "etapa encerrada".
	public static String paginaMinhaIdeia(HttpServletRequest request) {
		String licBasePath = request.getServletContext().getInitParameter("licBasePath");
		if (licBasePath == null) {
			licBasePath = "/LIC";
		}
		return licBasePath + "/minha-ideia.jsp";
	}
}
