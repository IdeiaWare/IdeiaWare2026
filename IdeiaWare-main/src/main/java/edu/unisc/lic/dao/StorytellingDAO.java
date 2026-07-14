package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class StorytellingDAO extends GenericDAO<Storytelling> {

    public List<Storytelling> listarParametro(Storytelling st) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Storytelling> consulta = builder.createQuery(Storytelling.class);
            Root<Storytelling> raiz = consulta.from(Storytelling.class);

            List<Predicate> predicados = new ArrayList<>();
            if (st.getUsuario().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("usuario"), st.getUsuario()));
            }

            if (st.getIdeia().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("ideia"), st.getIdeia()));
            }

            if (st.getStatus() != null) {
                predicados.add(builder.equal(raiz.get("status"), st.getStatus()));
            }

            consulta.select(raiz).where(predicados.toArray(new Predicate[0]));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }
}
