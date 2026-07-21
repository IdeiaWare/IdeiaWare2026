package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.util.HibernateUtil;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class CanvaDAO extends GenericDAO<Canva> {

    // DAO-FAIL-CLOSED: sem ideia definida, nao lista tudo
    public List<Canva> listarParametro(Canva canva) {
        if (canva.getIdeia().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Canva> consulta = builder.createQuery(Canva.class);
            Root<Canva> raiz = consulta.from(Canva.class);

            consulta.select(raiz).where(builder.equal(raiz.get("ideia"), canva.getIdeia()));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    public List<Canva> listarCanvaElement(Canva canva, String element) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Canva> consulta = builder.createQuery(Canva.class);
            Root<Canva> raiz = consulta.from(Canva.class);

            Predicate porIdeia = builder.equal(raiz.get("ideia"), canva.getIdeia());
            Predicate porAtributo = builder.equal(raiz.get("attribute"), element);

            consulta.select(raiz).where(builder.and(porIdeia, porAtributo));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

}
