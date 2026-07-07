package edu.unisc.lic.classes;

/**
 * REFAC-01: constantes dos status de Ideia e de Grupo, centralizadas num lugar
 * so (antes os codigos "PE", "VA", "ST"... ficavam espalhados como strings
 * magicas pelos servlets/DAOs, faceis de errar). Os VALORES sao exatamente os
 * mesmos de antes — nada muda no funcionamento, so a legibilidade/seguranca.
 *
 * OBS.: os JSPs continuam usando os literais ('VA', 'CV'...) em EL, pois EL nao
 * referencia constantes Java diretamente. A referencia textual fica no README.
 */
public final class StatusIdeia {

    // ----- Status da Ideia -----
    public static final String PENDENTE = "PE";            // aguardando validacao do admin
    public static final String VALIDADA = "VA";            // aceita pelo admin
    public static final String REJEITADA = "RE";           // rejeitada pelo admin
    public static final String EM_DESENVOLVIMENTO = "DE";  // colaboracao em andamento
    public static final String STORYTELLING = "ST";
    public static final String CAIXA_FERRAMENTAS = "CF";
    public static final String CANVAS = "CV";
    public static final String FINALIZADO = "FN";

    // ----- Status do Grupo -----
    public static final String GRUPO_ABERTO = "AB";        // aberto para novos colaboradores
    public static final String GRUPO_FECHADO = "FE";       // fechado

    // ----- Status do Vinculo (IdeiaUsuario) -- M.2: lista de espera de entrada no grupo -----
    public static final String VINCULO_PENDENTE = "P";     // pediu pra entrar, aguarda o lider
    public static final String VINCULO_APROVADO = "A";     // lider aprovou (ou lider/legado)
    public static final String VINCULO_REJEITADO = "R";    // lider recusou (com motivo, M.3)

    private StatusIdeia() {
    }
}
