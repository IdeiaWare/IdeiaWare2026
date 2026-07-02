package br.unisc.toolkit.classes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * SEC-23: validacao da assinatura HMAC do ideiaId (posta pelo LIC ao entrar na Caixa). O
 * AdminCookies so aceita o ideiaId se a assinatura bater -> o cookie deixa de ser FORJAVEL.
 *
 * IMPORTANTE: o SEGREDO TEM que ser IGUAL ao de edu.unisc.lic.classes.AssinaturaCaixa (LIC),
 * senao nenhuma assinatura valida. Externalizado via CAIXA_HMAC_SECRET.
 */
public class AssinaturaCaixa {

    // SEC-#5: segredo HMAC via env CAIXA_HMAC_SECRET. DEVE ser IGUAL ao do LIC (no Docker e a
    // mesma env do container). Default = valor legado p/ funcionar sem env.
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

    /** true se a assinatura confere com o HMAC do valor (comparacao em tempo constante). */
    public static boolean valida(String valor, String assinatura) {
        if (valor == null || assinatura == null) {
            return false;
        }
        String esperada = assinar(valor);
        if (esperada == null) {
            return false;
        }
        return MessageDigest.isEqual(
                esperada.getBytes(StandardCharsets.UTF_8),
                assinatura.getBytes(StandardCharsets.UTF_8));
    }
}
