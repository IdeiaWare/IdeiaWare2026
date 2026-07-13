package edu.unisc.lic.dao;

import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class LogColaboracaoDAO extends GenericDAO<LogColaboracao> {

    public List<LogColaboracao> listarParametro(LogColaboracao lc) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(LogColaboracao.class);

            if (lc.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", lc.getUsuario()));
            }
            if (lc.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", lc.getIdeia()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }

    }

    public LogColaboracao buscarDescricaoFinal(LogColaboracao lc) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(LogColaboracao.class);

            if (lc.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", lc.getUsuario()));
            }
            if (lc.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", lc.getIdeia()));
            }

            filtro.addOrder(Order.desc("codigo"));

            List<LogColaboracao> resultado = filtro.list();

            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }
}
