package br.unisc.toolkit.classes;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.service.IdeiaService;

// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas (CF) --
// checagem via IdeiaService (@Transactional), NAO via IdeiaDAO direto -- IdeiaDAOImpl nao
// tem sessao Hibernate propria, so funciona chamado de dentro de um metodo @Transactional
// do Service layer. Chamar o DAO direto do controller (1a tentativa) estourava
// "HibernateException: Could not obtain transaction-synchronized Session for current
// thread" -- mesmo risco que descartou a ideia do HandlerInterceptor, so' que manifestado
// de um jeito diferente (controller tambem nao e' @Transactional, so' o Service e').
public final class StatusGuard {

    private static final String CAIXA_FERRAMENTAS = "CF";

    private StatusGuard() {
    }

    public static boolean podeEscrever(IdeiaService ideiaService, Long ideiaCodigo) {
        if (ideiaCodigo == null) {
            return false;
        }
        Ideia ideia = ideiaService.getIdeia(ideiaCodigo);
        return ideia != null && CAIXA_FERRAMENTAS.equals(ideia.getStatus());
    }
}
