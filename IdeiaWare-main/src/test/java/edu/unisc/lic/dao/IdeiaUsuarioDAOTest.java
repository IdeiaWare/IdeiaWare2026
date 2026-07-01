package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class IdeiaUsuarioDAOTest {

    @Test
    @Ignore
    public void inserir() {
        UsuarioDAO uDao = new UsuarioDAO();
        IdeiaDAO iDao = new IdeiaDAO();
        IdeiaUsuarioDAO iuDao = new IdeiaUsuarioDAO();

        Usuario u = uDao.buscar(3L);
        Ideia i = iDao.buscar(1L);
        IdeiaUsuario iu = new IdeiaUsuario();

        iu.setUsuario(u);
        iu.setIdeia(i);

        iuDao.salvar(iu);

    }

    @Test
    @Ignore
    public void listar() {
        IdeiaUsuarioDAO iuDAO = new IdeiaUsuarioDAO();

        List<IdeiaUsuario> resultado = iuDAO.listar();

        for (IdeiaUsuario iu : resultado) {
            System.out.println(iu);
        }
    }

    @Test
    @Ignore
    public void listarParametro() {
        IdeiaUsuarioDAO iuDAO = new IdeiaUsuarioDAO();

//        Ideia i = new Ideia();
//        i.setCodigo(1L);
//        IdeiaUsuario iu = new IdeiaUsuario(null, i, null);
        Usuario u = new Usuario();
        u.setCodigo(1L);

        IdeiaUsuario iu = new IdeiaUsuario(u, null, null);

        List<IdeiaUsuario> resultado = iuDAO.listarParametro(iu);

        for (IdeiaUsuario ideiaUsuario : resultado) {
//            System.out.println(ideiaUsuario.getIdeia());
            System.out.println("\n\n" + ideiaUsuario.getIdeia().getUsuario().getNome());
        }

        System.out.println("\n\n");
    }

    @Test
    @Ignore
    public void listarIdeiasLider() {
        Usuario u = new UsuarioDAO().buscar(1L);
        IdeiaUsuario iu = new IdeiaUsuario();
        iu.setUsuario(u);

        List<IdeiaUsuario> resultado = new IdeiaUsuarioDAO().listarIdeiasLiderStorytelling(iu);

        System.out.println(resultado.size());

        for (IdeiaUsuario iuAux : resultado) {
            System.out.println(iuAux.getUsuario().getCodigo() + " - " + iuAux.getIdeia().getCodigo() + " - " + iuAux.getFlLider() + " - " + iuAux.getIdeia().getStatus());
        }

    }

}
