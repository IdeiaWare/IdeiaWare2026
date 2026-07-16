package edu.unisc.lic.classes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// RESET-TOKEN: token aleatorio de reset de senha + hash pra guardar no banco (nunca o token em claro,
// mesmo raciocinio de nunca guardar senha em claro -- se o banco vazar, o hash sozinho nao reseta nada).
public class TokenReset {

    private TokenReset() {
    }

    public static String gerar() {
        byte[] bytes = new byte[32];
        Constantes.random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String tokenEmClaro) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] h = sha256.digest(tokenEmClaro.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(h.length * 2);
            for (byte b : h) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel na JVM", e);
        }
    }
}
