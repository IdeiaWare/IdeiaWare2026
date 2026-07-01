/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unisc.lic.classes;

/**
 *
 * @author Gustavo Motta
 */

/**
 *
 * @author Gustavo Motta
 */

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CriptografaDados {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    // SEC-#5: chave AES via env AES_KEY; default = legado (so usado pelo cfgbanco.txt inexistente
    // + teste). Deve ter 16/24/32 bytes p/ AES.
    private static final String CHAVE = chaveAes();

    private static String chaveAes() {
        String k = System.getenv("AES_KEY");
        return (k != null && !k.isEmpty()) ? k : "1234567890123456";
    }


    public static String criptografar(String senha) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKey = new SecretKeySpec(CHAVE.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] senhaCriptografada = cipher.doFinal(senha.getBytes());
        return Base64.getEncoder().encodeToString(senhaCriptografada);
    }

    public static String descriptografar(String senhaCriptografada) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKey = new SecretKeySpec(CHAVE.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] senhaDecodificada = Base64.getDecoder().decode(senhaCriptografada);
        byte[] senhaDescriptografada = cipher.doFinal(senhaDecodificada);
        return new String(senhaDescriptografada);
    }


}