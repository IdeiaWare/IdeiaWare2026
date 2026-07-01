package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class IdeiaDAOTest {

    @Test
    @Ignore
    public void salvar() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar(1L);

        Ideia ideia = new Ideia(usuario, "Monitores Novos", "Os monitores ainda são de tubo, temos que nos adequar a novas ferramentas", "ag", "ab");
        ideia.setDtCriacao();

        System.out.println(ideia);

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        ideiaDAO.salvar(ideia);
    }

    @Test
    @Ignore
    public void listar() {
        Ideia i = new Ideia();
//        i.setTitulo("cadei");
//        i.setStatus("PE");
        i.setUsuario(new UsuarioDAO().buscar(1L));

        System.out.println(i);

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        List<Ideia> resultado = ideiaDAO.listarParametro(i);

        for (int j = 0; j < resultado.size(); j++) {
            System.out.println(resultado.get(j));
        }
    }

    @Test
    @Ignore
    public void buscar() {
        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(1L);

//        System.out.println(ideia.getCodigo() + " - " + ideia.getNome() + " | " + ideia.getDescricao() + " | " + ideia.getData() + " | " + ideia.getUsuario().getNome());
    }

    @Test
    @Ignore
    public void excluir() {
        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(4L);

        ideiaDAO.excluir(ideia);
    }

    @Test
    @Ignore
    public void editar() {
        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(5L);

        if (ideia == null) {
            System.out.println("ERROR 404");
        } else {
//            Usuario usuario = ideia.getUsuario();
//            usuario.setCodigo(1L);
//            ideia.setUsuario(usuario);
            ideia.setDescricao("Os monitores do projeto tem pouquisima taxa de refresh e estão com faixas pretas, presisamos de algo melhor");
            ideiaDAO.editar(ideia);
//            System.out.println(ideia.getCodigo() + " - " + ideia.getNome() + " | " + ideia.getDescricao() + " | " + ideia.getData() + " | " + ideia.getUsuario().getNome());
        }
    }
}
