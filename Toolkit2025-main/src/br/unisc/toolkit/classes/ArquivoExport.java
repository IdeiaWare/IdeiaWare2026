package br.unisc.toolkit.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

// PDF-DISCO
public class ArquivoExport {

	public static String salvar(String conteudoBase64OuDataUri, Long ideiaCodigo) throws IOException {
		String base64 = conteudoBase64OuDataUri.trim();
		int virgula = base64.indexOf(',');
		if (base64.startsWith("data:") && virgula >= 0) {
			base64 = base64.substring(virgula + 1);
		}
		byte[] bytes = Base64.getDecoder().decode(base64);

		String caminhoRelativo = Constantes.CAMINHO_EXPORT_CONHECIMENTO + ideiaCodigo + File.separator
				+ UUID.randomUUID().toString().replace("-", "") + ".pdf";

		File destino = new File(Constantes.caminhoExports() + caminhoRelativo);
		destino.getParentFile().mkdirs();
		try (FileOutputStream out = new FileOutputStream(destino)) {
			out.write(bytes);
		}
		return caminhoRelativo;
	}
}
