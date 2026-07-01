package edu.unisc.lic.dao;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.util.HibernateUtil;
import java.util.LinkedList;
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
public class IdeiaUsuarioDAO extends GenericDAO<IdeiaUsuario> {

    private Session sessao;
    private Transaction transacao;

    /**
     * Esse método busca e retorna
     *
     * @param iu - IdeiaUsuario
     * @return
     */
    public List<IdeiaUsuario> listarParametro(IdeiaUsuario iu) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<IdeiaUsuario> resultado = null;

        try {

            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);

            if (iu.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            }
            if (iu.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", iu.getIdeia()));
            }
            if (iu.getFlLider() != null) {
                filtro.add(Restrictions.eq("flLider", iu.getFlLider()));
            }

            // Ordena a listagem das "Minhas Ideias" do mais NOVO para o mais antigo
            // (ideia recem-cadastrada aparece em cima). So afeta este caso (filtro por
            // usuario, varias ideias); os callers que filtram por uma unica ideia
            // (membros de um grupo) nao mudam, pois todos tem o mesmo valor de "ideia".
            filtro.addOrder(Order.desc("ideia"));

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

    public List<IdeiaUsuario> listarIdeiasLiderStorytelling(IdeiaUsuario iu) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<IdeiaUsuario> resultado = new LinkedList<>();

        try {

            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);

            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            filtro.add(Restrictions.eq("flLider", "S"));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            List<IdeiaUsuario> resultadoAuxiliar = filtro.list();
            for (IdeiaUsuario iuAux : resultadoAuxiliar) {
                if (iuAux.getIdeia().getStatus().equals(StatusIdeia.STORYTELLING)) {
                    resultado.add(iuAux);
                }
            }

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
    
    /**
     * Retorna todas as ideias em status ST para o usuário — líderes e participantes.
     * Corrige STR-09: lista-storytelling.jsp só mostrava para líderes.
     */
    public List<IdeiaUsuario> listarTodasIdeiasStorytelling(IdeiaUsuario iu) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<IdeiaUsuario> resultado = new LinkedList<>();

        try {
            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);
            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            List<IdeiaUsuario> aux = filtro.list();
            for (IdeiaUsuario iuAux : aux) {
                if (StatusIdeia.STORYTELLING.equals(iuAux.getIdeia().getStatus())) {
                    resultado.add(iuAux);
                }
            }
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

    public List<IdeiaUsuario> listarCaixa(IdeiaUsuario iu) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<IdeiaUsuario> resultado = new LinkedList<>();

        try {

            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);

            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            // TK-LIST: a Caixa aparece p/ TODOS os participantes da ideia (nao so o lider).
            // Se da p/ entrar pelo "Minhas Ideias", tem que aparecer nesta listagem tambem.
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            List<IdeiaUsuario> resultadoAuxiliar = filtro.list();
            for (IdeiaUsuario iuAux : resultadoAuxiliar) {
                if (iuAux.getIdeia().getStatus().equals(StatusIdeia.CAIXA_FERRAMENTAS)) {
                    resultado.add(iuAux);
                }
            }

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
    
    public List<IdeiaUsuario> listarIdeiasCanva(IdeiaUsuario iu) {
        sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        transacao = sessao.beginTransaction();

        List<IdeiaUsuario> resultado = new LinkedList<>();

        try {

            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);

            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            filtro.add(Restrictions.eq("flLider", "S"));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            List<IdeiaUsuario> resultadoAuxiliar = filtro.list();
            for (IdeiaUsuario iuAux : resultadoAuxiliar) {
                // Status "CV" é definido pelo projeto Toolkit2025 (Caixa de Ferramentas)
                // no método IdeiaDAOImpl.finalize() quando a ideia sai da caixa.
                // É o status correto para listar no Canvas — NÃO alterar.
                if (StatusIdeia.CANVAS.equals(iuAux.getIdeia().getStatus())) {
                    resultado.add(iuAux);
                }
            }

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
