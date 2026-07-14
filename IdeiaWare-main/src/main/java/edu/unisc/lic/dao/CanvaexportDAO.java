package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.util.HibernateUtil;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class CanvaexportDAO extends GenericDAO<Canvaexport> {

    // DAO-FAIL-CLOSED: sem ideia definida nao ha o que listar (antes voltava a tabela inteira).
    public List<Canvaexport> listarParametro(Canvaexport canva) {
        if (canva.getIdeia().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Canvaexport> consulta = builder.createQuery(Canvaexport.class);
            Root<Canvaexport> raiz = consulta.from(Canvaexport.class);

            consulta.select(raiz).where(builder.equal(raiz.get("ideia"), canva.getIdeia()));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

}
