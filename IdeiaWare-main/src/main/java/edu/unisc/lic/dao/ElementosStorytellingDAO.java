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

/**
 *
 * @author viniciussdsilva
 */
public class ElementosStorytellingDAO extends GenericDAO<ElementosStorytelling> {

    /**
     * Lista todos os elementos de determinado storytelling e seu respectivo
     * tipo
     *
     * @param est
     * @return
     */
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

    /**
     * PERF-02: busca varios elementos por codigo NUMA SO query (Restrictions.in), em vez de
     * um buscar(codigo) por elemento. Usado pelo AutoSalvarStoryServlet para eliminar o N+1
     * do autosave do quadro de Storytelling.
     */
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

    /**
     * PERF-02: salva/atualiza uma lista inteira numa UNICA Session/Transaction (1 commit no
     * final), em vez de 1 editar() por elemento (cada um abrindo sua propria conexao).
     */
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

    /**
     * PERF-02: exclui uma lista inteira numa UNICA Session/Transaction, em vez de 1 excluir()
     * por elemento. Usado pelo SalvarAudioServlet (remove o audio anterior do storytelling).
     */
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

    /**
     * K.8 #7 (2026-07-06): substitui o(s) audio(s) antigo(s) do storytelling pelo novo
     * numa UNICA Session/Transaction. Antes, SalvarAudioServlet chamava excluirTodos(...)
     * e depois salvar(...) em 2 transacoes SEPARADAS -- uma falha exatamente entre as duas
     * apagava o audio antigo (ja commitado) sem o novo ser salvo, perda total do audio.
     * Agora ou os dois passos commitam juntos, ou nenhum commita (rollback).
     */
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
