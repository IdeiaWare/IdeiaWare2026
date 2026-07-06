package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author yanrodrigues
 */
public class CanvaexportDAO extends GenericDAO<Canvaexport> {

    /**
     * Esse método busca e retorna
     *
     * @param canva - Canvaexport
     * @return
     */
    public List<Canvaexport> listarParametro(Canvaexport canva) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(Canvaexport.class);

            if (canva.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", canva.getIdeia()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

}
