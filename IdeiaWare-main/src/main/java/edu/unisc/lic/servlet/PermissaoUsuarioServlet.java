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

/**
 *
 * @author lucas
 */
@WebServlet(name = "PermissaoUsuarioServlet", urlPatterns = {"/PermissaoUsuarioServlet"})
public class PermissaoUsuarioServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // RKM-01: sem esta checagem, qualquer usuário logado podia se promover a
        // administrador. Só um admin pode alterar permissões de usuários.
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao altera permissao (fecha a escalada de privilegio
        // por CSRF via GET: um admin com link malicioso promoveria um atacante a adm).
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
