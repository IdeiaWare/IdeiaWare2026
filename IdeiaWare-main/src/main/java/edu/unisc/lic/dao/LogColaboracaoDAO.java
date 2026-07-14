package edu.unisc.lic.dao;

import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class LogColaboracaoDAO extends GenericDAO<LogColaboracao> {

    public List<LogColaboracao> listarParametro(LogColaboracao lc) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<LogColaboracao> consulta = builder.createQuery(LogColaboracao.class);
            Root<LogColaboracao> raiz = consulta.from(LogColaboracao.class);

            List<Predicate> predicados = new ArrayList<>();
            if (lc.getUsuario().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("usuario"), lc.getUsuario()));
            }
            if (lc.getIdeia().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("ideia"), lc.getIdeia()));
            }

            consulta.select(raiz).where(predicados.toArray(new Predicate[0]));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }

    }

    public LogColaboracao buscarDescricaoFinal(LogColaboracao lc) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<LogColaboracao> consulta = builder.createQuery(LogColaboracao.class);
            Root<LogColaboracao> raiz = consulta.from(LogColaboracao.class);

            List<Predicate> predicados = new ArrayList<>();
            if (lc.getUsuario().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("usuario"), lc.getUsuario()));
            }
            if (lc.getIdeia().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("ideia"), lc.getIdeia()));
            }

            consulta.select(raiz)
                    .where(predicados.toArray(new Predicate[0]))
                    .orderBy(builder.desc(raiz.get("codigo")));

            List<LogColaboracao> resultado = sessao.createQuery(consulta).getResultList();

            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }
}
