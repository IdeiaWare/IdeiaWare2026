package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.util.HibernateUtil;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author yanrodrigues
 */
public class CanvaDAO extends GenericDAO<Canva> {

    private Session sessao;
    private Transaction transacao;

    /**
     * Esse método busca e retorna
     *
     * @param canva - Canva
     * @return
     */
    public List<Canva> listarParametro(Canva canva) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<Canva> resultado = null;

        try {

            Criteria filtro = sessao.createCriteria(Canva.class);

            if (canva.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", canva.getIdeia()));
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

    public List<Canva> listarCanvaElement(Canva canva, String element) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<Canva> resultado = new LinkedList<>();

        try {

            Criteria filtro = sessao.createCriteria(Canva.class);
            
            filtro.add(Restrictions.eq("ideia", canva.getIdeia()));
            
            filtro.add(Restrictions.eq("attribute", element));

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

}
