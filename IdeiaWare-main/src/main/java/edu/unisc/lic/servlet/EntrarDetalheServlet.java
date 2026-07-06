/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author USER
 */
public class EntrarDetalheServlet extends HttpServlet {

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
        
        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login. Antes o servlet nao checava sessao nenhuma.
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // COLM-05: valida o código antes de converter.
        long codigo;
        try {
            codigo = Long.parseLong(request.getParameter("codigo"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(codigo);
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        // AUTORIZACAO: "lider" nao vem mais de parametro do cliente (era confiado
        // direto -- dava p/ forjar lider=S em qualquer ideia so mudando o form antes
        // de enviar). Calculado no servidor a partir do vinculo real do usuario
        // logado com esta ideia especifica.
        Usuario usuarioLogado = new UsuarioDAO().buscar((Long) codigoUsuarioObj);
        List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(usuarioLogado, ideia, null));
        String lider = (vinculo != null && !vinculo.isEmpty()) ? vinculo.get(0).getFlLider() : "N";

        session.setAttribute("ideia", ideia);
        session.setAttribute("lider", lider);

        response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");

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
        processRequest(request, response);
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
