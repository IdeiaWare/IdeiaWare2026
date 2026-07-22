package edu.unisc.lic.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

public class ArquivoExport {

    // CONC-01: escreve em arquivo temp e move atomicamente pro destino
    public static String salvar(String conteudoBase64OuDataUri, String caminhoRelativo) throws IOException {
        String base64 = conteudoBase64OuDataUri.trim();
        int virgula = base64.indexOf(',');
        if (base64.startsWith("data:") && virgula >= 0) {
            base64 = base64.substring(virgula + 1);
        }
        byte[] bytes = Base64.getDecoder().decode(base64);

        File destino = new File(Constantes.caminhoExports() + caminhoRelativo);
        destino.getParentFile().mkdirs();
        File temp = new File(destino.getParentFile(), destino.getName() + "." + UUID.randomUUID() + ".tmp");
        try (FileOutputStream out = new FileOutputStream(temp)) {
            out.write(bytes);
        }
        Files.move(temp.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return caminhoRelativo;
    }

    public static boolean excluir(String caminhoRelativo) {
        if (caminhoRelativo == null || caminhoRelativo.trim().isEmpty()) {
            return false;
        }
        return new File(Constantes.caminhoExports() + caminhoRelativo).delete();
    }
}
