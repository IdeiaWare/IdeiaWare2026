package br.unisc.toolkit.classes;

import java.io.File;

public class Constantes {

	public static final String CAMINHO_EXPORT_CONHECIMENTO = "conhecimento" + File.separator;

	// PDF-DISCO
	public static String caminhoExports() {
		return System.getProperty("ideiaware.exports.dir",
				File.separator + "data" + File.separator + "ideiaware-exports") + File.separator;
	}
}
