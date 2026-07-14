package edu.unisc.lic.classes;

import java.io.File;
import java.security.SecureRandom;

public class Constantes {
    public static final String CAMINHO_IMAGENS_STORYTELLING = "imagensStorytelling" + File.separator;
    public static final String CAMINHO_FORMAS = "imagens" + File.separator;
    public static final String CAMINHO_CANVA = "canvas" + File.separator;

    public static final String CAMINHO_EXPORT_CANVA = "canva" + File.separator;
    public static final String CAMINHO_EXPORT_STORYTELLING = "storytelling" + File.separator;

    // PDF-DISCO
    public static String caminhoExports() {
        return System.getProperty("ideiaware.exports.dir",
                File.separator + "data" + File.separator + "ideiaware-exports") + File.separator;
    }

    public static final String LETRAS_MIN = "abcdefghijklmnopqrstuvwxyz";
    public static final String LETRAS_MAI = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMEROS = "0123456789";
    public static final String CARAC_ESP = "@$!%*?&";
    public static final String CARACTERES = LETRAS_MIN + LETRAS_MAI + NUMEROS + CARAC_ESP;
    public static final SecureRandom random = new SecureRandom();
}
