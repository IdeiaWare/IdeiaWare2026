package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PermissaoUsuarioServlet", urlPatterns = {"/PermissaoUsuarioServlet"})
public class PermissaoUsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // RKM-01: so admin altera permissao (antes, qualquer logado se promovia a admin).
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario sessionUser = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null || !"adm".equals(sessionUser.getPermissao())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // RKM-05: valida o parâmetro antes de converter/buscar
        String codigoAlterar = request.getParameter("codigoAlterar");
        if (codigoAlterar == null || codigoAlterar.trim().isEmpty()) {
            request.getRequestDispatcher("gerenciar-usuarios.jsp").forward(request, response);
            return;
        }

        Usuario usuario;
        try {
            usuario = usuarioDAO.buscar(Long.valueOf(codigoAlterar));
        } catch (NumberFormatException e) {
            request.getRequestDispatcher("gerenciar-usuarios.jsp").forward(request, response);
            return;
        }

        if (usuario == null) {
            request.getRequestDispatcher("gerenciar-usuarios.jsp").forward(request, response);
            return;
        }

        if (request.getParameterMap().containsKey("gestor")) {
            usuario.setPermissao("adm");
        } else {
            usuario.setPermissao("col");
        }
        usuarioDAO.editar(usuario);
        request.getRequestDispatcher("gerenciar-usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only (antes, GET dava pra escalar privilegio via CSRF/link).
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
