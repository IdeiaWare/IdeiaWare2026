package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.util.HibernateUtil;
import java.util.Collections;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ElementosStorytellingDAO extends GenericDAO<ElementosStorytelling> {

    public List<ElementosStorytelling> listarParametro(ElementosStorytelling est) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ElementosStorytelling.class);

            if (est.getStorytelling().getCodigo() != null) {
                filtro.add(Restrictions.eq("storytelling", est.getStorytelling()));
            }

            if (est.getTipo() != null) {
                filtro.add(Restrictions.eq("tipo", est.getTipo()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    public ElementosStorytelling ultimoAdicionado(ElementosStorytelling est) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ElementosStorytelling.class);

            if (est.getStorytelling().getCodigo() != null) {
                filtro.add(Restrictions.eq("storytelling", est.getStorytelling()));
            }

            if (est.getTipo() != null) {
                filtro.add(Restrictions.eq("tipo", est.getTipo()));
            }

            filtro.addOrder(Order.desc("codigo"));
            filtro.setMaxResults(1);

            // STM-03: evita IndexOutOfBounds quando não há elementos.
            List<ElementosStorytelling> resultado = filtro.list();
            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }

    // PERF-02: busca varios por codigo NUMA SO query (elimina o N+1 do autosave).
    @SuppressWarnings("unchecked")
    public List<ElementosStorytelling> buscarPorCodigos(List<Long> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ElementosStorytelling.class);
            filtro.add(Restrictions.in("codigo", codigos));

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    // PERF-02: salva a lista inteira numa UNICA transacao (1 commit, nao 1 por elemento).
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

    // PERF-02: exclui a lista inteira numa UNICA transacao (nao 1 por elemento).
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

    // K.8 #7: apaga o(s) audio(s) antigo(s) + salva o novo NUMA UNICA transacao (antes, perda total em falha no meio).
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
