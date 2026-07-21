package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.TokenReset;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// RESET-TOKEN: 2a etapa, valida token do link e troca a senha
@WebServlet(name = "RedefinirSenhaServlet", urlPatterns = {"/RedefinirSenhaServlet"})
public class RedefinirSenhaServlet extends HttpServlet {

    private Usuario buscarUsuarioValido(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarPorTokenHash(TokenReset.hash(token));
        if (usuario == null || usuario.getResetTokenExpira() == null) {
            return null;
        }
        if (usuario.getResetTokenExpira().before(new Date())) {
            return null;
        }
        return usuario;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        Usuario usuario = buscarUsuarioValido(token);

        if (usuario == null) {
            request.setAttribute("tokenInvalido", true);
        } else {
            request.setAttribute("token", token);
        }
        request.getRequestDispatcher("redefinir-senha.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String token = request.getParameter("token");
        String novaSenha = request.getParameter("senha");
        String confirmaSenha = request.getParameter("senha2");

        Usuario usuario = buscarUsuarioValido(token);
        if (usuario == null) {
            request.setAttribute("tokenInvalido", true);
            request.getRequestDispatcher("redefinir-senha.jsp").forward(request, response);
            return;
        }

        if (novaSenha == null || novaSenha.isEmpty() || !novaSenha.equals(confirmaSenha)) {
            request.setAttribute("token", token);
            request.setAttribute("senhasNaoConferem", true);
            request.getRequestDispatcher("redefinir-senha.jsp").forward(request, response);
            return;
        }

        // RESET-TOKEN: troca a senha e invalida o token, uso unico
        usuario.setSenha(novaSenha, true);
        usuario.setResetTokenHash(null);
        usuario.setResetTokenExpira(null);
        new UsuarioDAO().editar(usuario);

        request.setAttribute("SenhaRedefinidaComSucesso", true);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Confirma o reset de senha via token e define a nova senha.";
    }
}
