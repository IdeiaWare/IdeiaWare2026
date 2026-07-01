/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
@WebServlet(name = "AnonimizaUsuarioServlet", urlPatterns = {"/AnonimizaUsuarioServlet"})
public class AnonimizaUsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            HttpSession session = request.getSession(true);
            
            Usuario usuario = new Usuario();
            usuario.setCodigo((Long) session.getAttribute("codigoUsuario"));

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

            // RKM-04: valida a senha antes de usá-la para evitar comportamento
            // indefinido quando o parâmetro não é enviado.
            String senhaAtual = request.getParameter("senhaAtual");
            if (senhaAtual == null || senhaAtual.isEmpty()) {
                request.setAttribute("respostaSenhaInvalida", true);
                request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
                return;
            }

            // SEC-22: verifica a senha atual com bcrypt (checaSenha).
            if (!usuario.checaSenha(senhaAtual)) {
                request.setAttribute("respostaSenhaInvalida", true);
                request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                return;
            }
            
            usuario.AnonimizaDadosPessoais();
            usuarioDAO.editar(usuario);
            session.invalidate();
        
            request.getRequestDispatcher("login.jsp").forward(request, response); 
        }
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
        // SEC-18: POST-only. GET nao anonimiza (evita CSRF via GET).
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
