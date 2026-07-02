package edu.unisc.lic.classes;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * SEC-23: assinatura HMAC do ideiaId, compartilhada entre o LIC e o Toolkit. O LIC ASSINA
 * o ideiaId ao entrar na Caixa (EntrarCaixaServlet); o Toolkit VALIDA a assinatura em todo
 * acesso (AdminCookies) -> o cookie ideiaId deixa de ser FORJAVEL: so vale um ideiaId que o
 * LIC autorizou (usuario logado + participante da ideia). Antes, qualquer um setava o cookie
 * ideiaId=N na mao e acessava/editava os dados de qualquer ideia no Toolkit.
 *
 * IMPORTANTE: o SEGREDO DEVE ser IGUAL ao do br.unisc.toolkit.classes.AssinaturaCaixa.
 * Externalizado via CAIXA_HMAC_SECRET (mesmo container no Docker = mesma env automaticamente).
 */
public class AssinaturaCaixa {

    // SEC-#5: segredo HMAC via env CAIXA_HMAC_SECRET. DEVE ser IGUAL no LIC e no Toolkit (no
    // Docker e a mesma env do container). Default = valor legado p/ funcionar sem env.
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
            return null;
        }
    }
}
