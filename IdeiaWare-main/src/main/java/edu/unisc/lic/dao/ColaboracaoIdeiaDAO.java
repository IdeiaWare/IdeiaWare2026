package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
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

    public int quantidadeMes(ColaboracaoIdeia ci) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.asc("dtModificacao"));

            return filtro.list().size();

        } finally {
            sessao.close();
        }
    }
}
