package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class UsuarioDAO extends GenericDAO<Usuario> {

    /**
     * Esse método busca e retorna resultados referentes ao nome, usuario,
     * senha, permissão e código do usuario.
     *
     * @param usuario
     * @param like (true: parecido; false: idêntico)
     * @return
     */
    public List<Usuario> listarParametro(Usuario usuario, boolean like) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(Usuario.class);

            if (usuario.getUsuario() != null) {
                if (like) {
                    filtro.add(Restrictions.like("usuario", "%" + usuario.getUsuario() + "%"));
                } else {
                    filtro.add(Restrictions.eq("usuario", usuario.getUsuario()));
                }
            }
            if (usuario.getNome() != null) {
                if (like) {
                    filtro.add(Restrictions.like("nome", "%" + usuario.getNome() + "%"));
                } else {
                    filtro.add(Restrictions.eq("nome", usuario.getNome()));
                }
            }
            if (usuario.getSenha() != null) {
                filtro.add(Restrictions.eq("senha", usuario.getSenha()));
            }
            if (usuario.getPermissao() != null) {
                filtro.add(Restrictions.eq("permissao", usuario.getPermissao()));
            }
            // LucasFreitag 2024
            if (usuario.getEmail() != null){
                filtro.add(Restrictions.eq("email", usuario.getEmail()));
            }
            if (usuario.getAnonimizado()!= null){
                filtro.add(Restrictions.eq("anonimizado", usuario.getAnonimizado()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }
}
