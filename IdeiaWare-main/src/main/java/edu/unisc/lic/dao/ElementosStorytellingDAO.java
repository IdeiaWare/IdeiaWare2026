package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author viniciussdsilva
 */
public class ElementosStorytellingDAO extends GenericDAO<ElementosStorytelling> {

    private Session sessao;
    private Transaction transacao;

    /**
     * Lista todos os elementos de determinado storytelling e seu respectivo
     * tipo
     *
     * @param est
     * @return
     */
    public List<ElementosStorytelling> listarParametro(ElementosStorytelling est) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<ElementosStorytelling> resultado = null;

        try {
            Criteria filtro = sessao.createCriteria(ElementosStorytelling.class);

            if (est.getStorytelling().getCodigo() != null) {
                filtro.add(Restrictions.eq("storytelling", est.getStorytelling()));
            }

            if (est.getTipo() != null) {
                filtro.add(Restrictions.eq("tipo", est.getTipo()));
            }

            resultado = filtro.list();

        } catch (HibernateException e) {
            if (this.transacao.isActive()) {
                this.transacao.rollback();
            }
        } finally {
            try {
                if (sessao.isOpen()) {
                    sessao.close();
                }
            } catch (HibernateException e) {
                System.out.println("Erro ao fechar a operação. Mensagem:" + e.getMessage());
            }
        }

        return resultado;
    }

    public ElementosStorytelling ultimoAdicionado(ElementosStorytelling est) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<ElementosStorytelling> resultado = null;

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

            resultado = filtro.list();

            // STM-03: evita IndexOutOfBounds quando não há elementos.
            return (resultado != null && !resultado.isEmpty()) ? resultado.get(0) : null;

        } catch (RuntimeException erro) {
            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }
    }
}
