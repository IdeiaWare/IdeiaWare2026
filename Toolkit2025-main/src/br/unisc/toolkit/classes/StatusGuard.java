package br.unisc.toolkit.classes;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.service.IdeiaService;

// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas (CF).
// UX-TOOLKIT-STATUS-GUARD-HIBERNATE-FIX: usa IdeiaService (@Transactional), nao IdeiaDAO direto.
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
