/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.EnvioEmail;
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

/**
 *
 * @author lucas
 */
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/ResetPasswordServlet"})
public class ResetPasswordServlet extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            Boolean resp = true;
            
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getParameter("email"));
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> lista = usuarioDAO.listarParametro(usuario, false);
            if (lista.size() == 1) {
                usuario = lista.get(0);
                String novaSenha = usuario.ResetaSenha();

                usuarioDAO.editar(usuario);
                String textoEmail = "Olá "+usuario.getNome()+",\n"+
                             "Recebemos uma solicitação para redefinir a senha na ferramenta IdeiaWare.\n\n" +
                             "Por favor, utilize a senha temporária abaixo para acessar sua conta:\n\n"+
                             "Senha Temporária: "+novaSenha+"\n\n" +
                             "IMPORTANTE: Por motivos de segurança, recomendamos que você altere essa senha temporária assim que fizer o login.\n\n"+
                             "Se você não solicitou essa alteração, por favor, entre em contato imediatamente com o administrador.";

                EnvioEmail.EnviaEmail(usuario.getEmail(), "Redefinição de senha - IdeiaWare", textoEmail);
            }

            // SEC-19 (anti-enumeracao): a resposta e SEMPRE a mesma, exista ou nao o
            // e-mail -> nao da p/ descobrir quais e-mails estao cadastrados. Antes,
            // e-mail inexistente mostrava "E-mail nao cadastrado" = vazamento.
            // PENDENTE (fila de auth/bcrypt): trocar o RESET IMEDIATO por um LINK com
            // token de expiracao (hoje qualquer um reseta a senha de quem souber o
            // e-mail = lockout da conta) + rate-limit (anti email-bombing).
            request.setAttribute("SucessoRedefinicaoSenha", true);
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
        // SEC-18: POST-only. GET nao redefine senha (evita CSRF via GET e acao por link).
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
