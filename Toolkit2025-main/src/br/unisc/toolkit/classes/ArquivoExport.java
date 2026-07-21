package br.unisc.toolkit.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

// PDF-DISCO
public class ArquivoExport {

	// CONC-01: mesma escrita atomica (temp + move) ja usada no lado LIC -- protege contra
	// arquivo truncado/corrompido se o processo cair no meio da escrita (o UUID no nome ja
	// evita colisao de 2 exports simultaneos, mas nao evita corrupcao de escrita parcial).
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
		File temp = new File(destino.getParentFile(), destino.getName() + "." + UUID.randomUUID() + ".tmp");
		try (FileOutputStream out = new FileOutputStream(temp)) {
			out.write(bytes);
		}
		Files.move(temp.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		return caminhoRelativo;
	}
}
