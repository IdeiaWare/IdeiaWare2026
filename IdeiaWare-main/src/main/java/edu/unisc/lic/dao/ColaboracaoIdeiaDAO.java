package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ColaboracaoIdeia;
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
public class ColaboracaoIdeiaDAO extends GenericDAO<ColaboracaoIdeia> {

    private Session sessao;
    private Transaction transacao;

    public List<ColaboracaoIdeia> listarParametro(ColaboracaoIdeia ci) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<ColaboracaoIdeia> resultado = null;

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.asc("codigo"));

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

    public ColaboracaoIdeia ultimaColab(ColaboracaoIdeia ci) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<ColaboracaoIdeia> resultado = null;

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.desc("dtModificacao"));
            filtro.setMaxResults(1);

            resultado = filtro.list();

            // RET-14: retorna null em vez de estourar IndexOutOfBounds se nao
            // houver colaboracoes (defensivo).
            return (resultado != null && !resultado.isEmpty()) ? resultado.get(0) : null;

        } catch (RuntimeException erro) {
            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }
    }

    public int quantidadeMes(ColaboracaoIdeia ci) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        int resultado = 0;

        try {
            Criteria filtro = sessao.createCriteria(ColaboracaoIdeia.class);

            if (ci.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ci.getIdeia()));
            }

            filtro.addOrder(Order.asc("dtModificacao"));

            resultado = filtro.list().size();

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
