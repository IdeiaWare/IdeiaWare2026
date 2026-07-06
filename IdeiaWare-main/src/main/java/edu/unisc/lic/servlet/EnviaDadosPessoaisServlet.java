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
@WebServlet(name = "EnviaDadosPessoaisServlet", urlPatterns = {"/EnviaDadosPessoaisServlet"})
public class EnviaDadosPessoaisServlet extends HttpServlet {

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
            
            HttpSession session = request.getSession(true);

            // RET-14: exige login. Sem codigoUsuario na sessao, buscar(null)
            // retornava null e usuario.getNome() dava NPE/500.
            Object codigoObj = session.getAttribute("codigoUsuario");
            if (codigoObj == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscar((Long) codigoObj);
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            String textoEmail = "Olá "+usuario.getNome()+",\n"+
                             "Recebemos sua solicitação para obter seus dados pessoais na ferramenta IdeiaWare.\n\n" +
                             "Segue dados pessoais que temos da sua conta:\n"+
                             usuario.getDadosPessoais()+"\n\n" +
                             "Se você não solicitou essas informação, por favor, entre em contato imediatamente com o administrador.";
            
            // RET-14: IOException de rede no SendGrid nao era capturada -> subia sem
            // tratamento em vez de cair no mesmo fluxo de erro amigavel que ja existia
            // pra falha de status (linha abaixo).
            Boolean resp;
            try {
                resp = EnvioEmail.EnviaEmail(usuario.getEmail(), "Dados pessoais - IdeiaWare", textoEmail);
            } catch (IOException ex) {
                resp = false;
            }

            if (resp)
                request.setAttribute("EnviouEmail", true);
            else
                request.setAttribute("ErroEnvioEmail", true);
            
            request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
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
        // SEC-18: POST-only. Antes um GET tambem disparava o envio de e-mail com
        // dados pessoais -- unico servlet do grupo sem essa trava, o que permitia
        // disparar o envio via CSRF por GET (ex.: <img src="EnviaDadosPessoaisServlet">
        // numa pagina de terceiros, enquanto a vitima estivesse logada). A tela
        // (index-perfil.jsp) ja usa form method="POST", entao isso nao muda o uso normal.
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
