package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class IdeiaDAO extends GenericDAO<Ideia> {

    // Sobrescrito pra listar da ideia mais NOVA pra mais antiga.
    @Override
    public List<Ideia> listar() {
        Session s = HibernateUtil.getFabricaDeSessoes().openSession();
        try {
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery<Ideia> consulta = builder.createQuery(Ideia.class);
            Root<Ideia> raiz = consulta.from(Ideia.class);
            consulta.select(raiz).orderBy(builder.desc(raiz.get("codigo")));
            return s.createQuery(consulta).getResultList();
        } finally {
            s.close();
        }
    }

    public List<Ideia> listarParametro(Ideia ideia) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Ideia> consulta = builder.createQuery(Ideia.class);
            Root<Ideia> raiz = consulta.from(Ideia.class);

            List<Predicate> predicados = new ArrayList<>();
            if (ideia.getStatus() != null) {
                predicados.add(builder.equal(raiz.get("status"), ideia.getStatus()));
            }
            if (ideia.getTitulo() != null) {
                predicados.add(builder.equal(raiz.get("titulo"), ideia.getTitulo()));
            }
            if (ideia.getUsuario().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("usuario"), ideia.getUsuario()));
            }

            // Mais NOVO pro mais antigo ('codigo' e auto-incremento).
            consulta.select(raiz)
                    .where(predicados.toArray(new Predicate[0]))
                    .orderBy(builder.desc(raiz.get("codigo")));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    // COL-10/COL-14: ideias VA/AB que o usuario ainda nao participa, numa unica query HQL.
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

    // K.8 #5: Ideia + vinculo de lideranca NUMA UNICA transacao (antes, falha deixava Ideia orfa).
    public void criarComLider(Ideia ideia, IdeiaUsuario vinculoLider) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            sessao.save(ideia);
            vinculoLider.setIdeia(ideia);
            sessao.save(vinculoLider);
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
