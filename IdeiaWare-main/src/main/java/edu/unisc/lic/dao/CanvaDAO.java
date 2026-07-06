package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author yanrodrigues
 */
public class CanvaDAO extends GenericDAO<Canva> {

    /**
     * Esse método busca e retorna
     *
     * @param canva - Canva
     * @return
     */
    public List<Canva> listarParametro(Canva canva) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(Canva.class);

            if (canva.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", canva.getIdeia()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    public List<Canva> listarCanvaElement(Canva canva, String element) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(Canva.class);

            filtro.add(Restrictions.eq("ideia", canva.getIdeia()));

            filtro.add(Restrictions.eq("attribute", element));

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

}
