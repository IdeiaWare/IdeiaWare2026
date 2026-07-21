package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ElementosStorytellingDAO extends GenericDAO<ElementosStorytelling> {

    // DAO-FAIL-CLOSED: sem storytelling definido, nao lista tudo
    public List<ElementosStorytelling> listarParametro(ElementosStorytelling est) {
        if (est.getStorytelling().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ElementosStorytelling> consulta = builder.createQuery(ElementosStorytelling.class);
            Root<ElementosStorytelling> raiz = consulta.from(ElementosStorytelling.class);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(builder.equal(raiz.get("storytelling"), est.getStorytelling()));
            if (est.getTipo() != null) {
                predicados.add(builder.equal(raiz.get("tipo"), est.getTipo()));
            }

            consulta.select(raiz).where(predicados.toArray(new Predicate[0]));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    public ElementosStorytelling ultimoAdicionado(ElementosStorytelling est) {
        if (est.getStorytelling().getCodigo() == null) {
            return null;
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ElementosStorytelling> consulta = builder.createQuery(ElementosStorytelling.class);
            Root<ElementosStorytelling> raiz = consulta.from(ElementosStorytelling.class);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(builder.equal(raiz.get("storytelling"), est.getStorytelling()));
            if (est.getTipo() != null) {
                predicados.add(builder.equal(raiz.get("tipo"), est.getTipo()));
            }

            consulta.select(raiz)
                    .where(predicados.toArray(new Predicate[0]))
                    .orderBy(builder.desc(raiz.get("codigo")));

            // STM-03: evita IndexOutOfBounds sem elementos
            List<ElementosStorytelling> resultado = sessao.createQuery(consulta).setMaxResults(1).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }

    // PERF-02: busca varios por codigo numa so query
    public List<ElementosStorytelling> buscarPorCodigos(List<Long> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ElementosStorytelling> consulta = builder.createQuery(ElementosStorytelling.class);
            Root<ElementosStorytelling> raiz = consulta.from(ElementosStorytelling.class);

            consulta.select(raiz).where(raiz.<Long>get("codigo").in(codigos));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    // PERF-02: salva a lista inteira numa unica transacao
    public void salvarLote(List<ElementosStorytelling> elementos) {
        if (elementos == null || elementos.isEmpty()) {
            return;
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            for (ElementosStorytelling e : elementos) {
                sessao.update(e);
            }
            transacao.commit();

        } catch (RuntimeException erro) {
            if (transacao != null) {
                transacao.rollback();
            }
            throw erro;
        } finally {
            sessao.close();
        }
    }

    // PERF-02: exclui a lista inteira numa unica transacao
    public void excluirTodos(List<ElementosStorytelling> elementos) {
        if (elementos == null || elementos.isEmpty()) {
            return;
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            for (ElementosStorytelling e : elementos) {
                sessao.delete(e);
            }
            transacao.commit();

        } catch (RuntimeException erro) {
            if (transacao != null) {
                transacao.rollback();
            }
            throw erro;
        } finally {
            sessao.close();
        }
    }

    // K.8 #7: apaga audios antigos + salva o novo numa unica transacao
    public void substituirAudio(List<ElementosStorytelling> antigos, ElementosStorytelling novo) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            if (antigos != null) {
                for (ElementosStorytelling e : antigos) {
                    sessao.delete(e);
                }
            }
            sessao.save(novo);
            transacao.commit();

        } catch (RuntimeException erro) {
            if (transacao != null) {
                transacao.rollback();
            }
            throw erro;
        } finally {
            sessao.close();
        }
    }
}
