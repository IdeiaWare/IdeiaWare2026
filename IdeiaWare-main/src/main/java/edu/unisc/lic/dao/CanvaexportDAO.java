package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.util.HibernateUtil;
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
public class CanvaexportDAO extends GenericDAO<Canvaexport> {

    private Session sessao;
    private Transaction transacao;

    /**
     * Esse método busca e retorna
     *
     * @param canva - Canvaexport
     * @return
     */
    public List<Canvaexport> listarParametro(Canvaexport canva) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<Canvaexport> resultado = null;

        try {

            Criteria filtro = sessao.createCriteria(Canvaexport.class);

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

}
