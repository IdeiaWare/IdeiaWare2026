package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author viniciussdsilva
 */
public class ColaboracaoIdeiaDAO extends GenericDAO<ColaboracaoIdeia> {

    public List<ColaboracaoIdeia> listarParametro(ColaboracaoIdeia ci) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.asc("codigo"));

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    /**
     * M.6 (2026-07-06): retorna TODAS as colaboracoes da ideia com codigo > ultimoCodigo,
     * em ordem crescente. Substitui o uso de ultimaColab() no polling do colaboracao.jsp,
     * que so trazia a ULTIMA -- se 2 chegassem entre 2 polls, a do meio se perdia ate um
     * F5, e o protocolo por CONTAGEM abria uma corrida que duplicava a colaboracao recem
     * enviada (o poll trazia a mesma que o handler de envio ja tinha adicionado). Com
     * codigo > ultimoCodigo, o cliente so renderiza o que ainda nao viu.
     */
    public List<ColaboracaoIdeia> listarAposCodigo(ColaboracaoIdeia ci, Long ultimoCodigo) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }
            filtro.add(Restrictions.gt("codigo", ultimoCodigo == null ? 0L : ultimoCodigo));
            filtro.addOrder(Order.asc("codigo"));

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    public ColaboracaoIdeia ultimaColab(ColaboracaoIdeia ci) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.desc("dtModificacao"));
            filtro.setMaxResults(1);

            // RET-14: retorna null em vez de estourar IndexOutOfBounds se nao
            // houver colaboracoes (defensivo).
            List<ColaboracaoIdeia> resultado = filtro.list();
            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }

    // REVISAO 2026-07-07: renomeado de quantidadeMes -- o nome prometia contar so as do
    // MES, mas o metodo nunca filtrou por data (so por ideia), sempre retornou o TOTAL
    // historico. Sem callers em producao (achado da varredura) -- renomeado pra refletir o
    // que de fato faz, e trocado list().size() (carrega todas as entidades so pra contar)
    // por Projections.rowCount() (COUNT no banco, sem trazer linha nenhuma pra memoria).
    public int quantidadeTotal(ColaboracaoIdeia ci) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.setProjection(Projections.rowCount());

            return ((Number) filtro.uniqueResult()).intValue();

        } finally {
            sessao.close();
        }
    }
}
