package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class StorytellingDAO extends GenericDAO<Storytelling> {

    public List<Storytelling> listarParametro(Storytelling st) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(Storytelling.class);

            if (st.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", st.getUsuario()));
            }

            if (st.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", st.getIdeia()));
            }

            if (st.getStatus() != null) {
                filtro.add(Restrictions.eq("status", st.getStatus()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }
}
