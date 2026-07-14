package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class UsuarioDAO extends GenericDAO<Usuario> {

    public List<Usuario> listarParametro(Usuario usuario, boolean like) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Usuario> consulta = builder.createQuery(Usuario.class);
            Root<Usuario> raiz = consulta.from(Usuario.class);

            List<Predicate> predicados = new ArrayList<>();

            if (usuario.getUsuario() != null) {
                if (like) {
                    predicados.add(builder.like(raiz.get("usuario"), "%" + usuario.getUsuario() + "%"));
                } else {
                    predicados.add(builder.equal(raiz.get("usuario"), usuario.getUsuario()));
                }
            }
            if (usuario.getNome() != null) {
                if (like) {
                    predicados.add(builder.like(raiz.get("nome"), "%" + usuario.getNome() + "%"));
                } else {
                    predicados.add(builder.equal(raiz.get("nome"), usuario.getNome()));
                }
            }
            if (usuario.getSenha() != null) {
                predicados.add(builder.equal(raiz.get("senha"), usuario.getSenha()));
            }
            if (usuario.getPermissao() != null) {
                predicados.add(builder.equal(raiz.get("permissao"), usuario.getPermissao()));
            }
            if (usuario.getEmail() != null) {
                predicados.add(builder.equal(raiz.get("email"), usuario.getEmail()));
            }
            if (usuario.getAnonimizado() != null) {
                predicados.add(builder.equal(raiz.get("anonimizado"), usuario.getAnonimizado()));
            }

            consulta.select(raiz).where(predicados.toArray(new Predicate[0]));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }
}
