package edu.unisc.lic.classes;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// SEC-23: assinatura HMAC do ideiaId (LIC assina, Toolkit valida) -- antes, cookie ideiaId forjavel.
// IMPORTANTE: o SEGREDO DEVE ser IGUAL ao do br.unisc.toolkit.classes.AssinaturaCaixa.
public class AssinaturaCaixa {

    // SEGREDO via env CAIXA_HMAC_SECRET (mesmo container no Docker = mesma env automaticamente).
    private static final String SEGREDO = segredo();

    private static String segredo() {
        String s = System.getenv("CAIXA_HMAC_SECRET");
        return (s != null && !s.isEmpty()) ? s : "7f3a9c1e8b5d2406af91e7c3b8d54a02e6f1a9c4d7b3e80f";
    }

    public static String assinar(String valor) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SEGREDO.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] h = mac.doFinal(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(h.length * 2);
            for (byte b : h) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            // Propaga em vez de virar cookie ideiaSig nulo/quebrado (degradaria a seguranca em silencio).
            throw new IllegalStateException("Falha ao gerar assinatura HMAC da Caixa de Ferramentas", e);
        }
    }
}
