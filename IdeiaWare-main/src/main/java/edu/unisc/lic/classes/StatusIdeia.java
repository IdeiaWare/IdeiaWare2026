package edu.unisc.lic.classes;

// REFAC-01: constantes dos status, antes strings magicas espalhadas pelo codigo
public final class StatusIdeia {

    public static final String PENDENTE = "PE";
    public static final String VALIDADA = "VA";
    public static final String REJEITADA = "RE";
    public static final String EM_DESENVOLVIMENTO = "DE";
    public static final String STORYTELLING = "ST";
    public static final String CAIXA_FERRAMENTAS = "CF";
    public static final String CANVAS = "CV";
    public static final String FINALIZADO = "FN";

    public static final String GRUPO_ABERTO = "AB";
    public static final String GRUPO_FECHADO = "FE";

    public static final String VINCULO_PENDENTE = "P";
    public static final String VINCULO_APROVADO = "A";
    public static final String VINCULO_REJEITADO = "R";

    private StatusIdeia() {
    }
}
