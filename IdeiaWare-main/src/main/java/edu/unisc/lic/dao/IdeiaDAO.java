package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class IdeiaDAO extends GenericDAO<Ideia> {
    
    private Session sessao;
    private Transaction transacao;

    /**
     * Retenção do Conhecimento (lista-ideia-gerenciamento) usa listar(): sobrescrito
     * para listar as ideias da mais NOVA para a mais antiga (codigo é auto-incremento).
     */
    @Override
    public List<Ideia> listar() {
        Session s = HibernateUtil.getFabricaDeSessoes().openSession();
        try {
            Criteria filtro = s.createCriteria(Ideia.class);
            filtro.addOrder(Order.desc("codigo"));
            return filtro.list();
        } finally {
            s.close();
        }
    }

    /**
     * Esse método busca e retorna resultados referentes ao status, codigo da
     * ideia, título, descricao, etc.
     *
     * @param ideia
     * @return
     */
    public List<Ideia> listarParametro(Ideia ideia) {
        this.sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        this.transacao = sessao.beginTransaction();
        List<Ideia> resultado = null;

        try {

            Criteria filtro = sessao.createCriteria(Ideia.class);

            if (ideia.getStatus() != null) {
                filtro.add(Restrictions.eq("status", ideia.getStatus()));
            }
            if (ideia.getTitulo() != null) {
                filtro.add(Restrictions.eq("titulo", ideia.getTitulo()));
            }
            if (ideia.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", ideia.getUsuario()));
            }

            // Listagens do mais NOVO para o mais antigo (ex.: ideias pendentes de
            // validacao). 'codigo' e auto-incremento, entao desc = recem-cadastradas
            // em cima. O size()-check em lista-ideia-gerenciamento nao e afetado.
            filtro.addOrder(Order.desc("codigo"));

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

    /**
     * Retorna ideias com status=VA e statusGrupo=AB das quais o usuário
     * ainda não participa. Substitui a combinação de ideiaDAO.listar() +
     * N chamadas ao IdeiaUsuarioDAO, resolvendo COL-10 e COL-14 com uma
     * única query HQL.
     *
     * @param usuario usuário logado
     * @return lista de ideias disponíveis para participação, ordenadas por data
     */
    @SuppressWarnings("unchecked")
    public List<Ideia> listarIdeiasDisponiveis(Usuario usuario) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        try {
            String hql = "FROM Ideia i "
                    + "WHERE i.status = 'VA' "
                    + "AND i.statusGrupo = 'AB' "
                    + "AND i NOT IN ("
                    + "  SELECT iu.ideia FROM IdeiaUsuario iu WHERE iu.usuario = :usuario"
                    + ") "
                    + "ORDER BY i.dtCriacao DESC";
            return sessao.createQuery(hql)
                    .setParameter("usuario", usuario)
                    .list();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            sessao.close();
        }
    }
}
