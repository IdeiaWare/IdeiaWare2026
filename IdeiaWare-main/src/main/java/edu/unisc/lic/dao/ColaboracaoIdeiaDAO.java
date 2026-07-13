package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

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

    // M.6: colaboracoes com codigo > ultimoCodigo (antes, protocolo por contagem perdia/duplicava).
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

            // RET-14: retorna null em vez de estourar se nao houver colaboracoes.
            List<ColaboracaoIdeia> resultado = filtro.list();
            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }

    // GT-12: renomeado de quantidadeMes (nunca filtrou por mes) + COUNT no banco em vez de carregar tudo.
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
