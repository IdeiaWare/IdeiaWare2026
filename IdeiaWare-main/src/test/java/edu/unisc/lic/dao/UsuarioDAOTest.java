package edu.unisc.lic.dao;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.unisc.lic.domain.Usuario;

public class UsuarioDAOTest {
    
    @Test
    @Ignore
    public void insert() {
        Usuario usuario = new Usuario("Marina Flores", "marinaf", "flores", "col", "maria@email.com");

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.salvar(usuario);

    }

    @Test
    @Ignore
    public void listar() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        List<Usuario> resultado = usuarioDAO.listar();

        for (Usuario usuario : resultado) {
            System.out.println(usuario);
        }

    }

    @Test
    @Ignore
    public void buscar() {
        Long codigo = 2L;

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar(codigo);

        if (usuario == null) {
            System.out.println("Usuario nao encontrado!");
        } else {
            System.out.println(usuario);
        }

    }

    @Test
    @Ignore
    public void excluir() {

        Long codigo = 4L;

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar(codigo);

        usuarioDAO.excluir(usuario);

    }

    @Test
    @Ignore
    public void editar() {
        Long codigo = 3L;

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar(codigo);

        usuario.setNome("Eduardo Jacobi");

        usuarioDAO.editar(usuario);

    }

    @Test
    @Ignore
    public void listarParametro() {
        Usuario usuario = new Usuario();
        usuario.setUsuario("vsdsilva2");
//        usuario.setSenha("vinicius123");
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        List<Usuario> resultado = usuarioDAO.listarParametro(usuario, false);

        if (resultado.isEmpty()) {
            System.out.println("inserir usuario");
        } else {
            System.out.println("usuario ja existente");
        }
        
    }

}
