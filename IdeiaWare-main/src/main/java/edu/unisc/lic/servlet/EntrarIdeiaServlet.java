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

/**
 *
 * @author USER
 */
public class EntrarIdeiaServlet extends HttpServlet {

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

        // COLM-05: exige sessão válida e código de ideia numérico.
        Object codigoUsuarioObj = request.getSession().getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        long codigoIdeia;
        try {
            codigoIdeia = Long.parseLong(request.getParameter("codigo"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar((Long) codigoUsuarioObj);

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(codigoIdeia);

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

        // COL-DUP: nao cria vinculo DUPLICADO. Antes, clicar "Entrar" 2x inscrevia o
        // mesmo usuario na mesma ideia duas vezes (membro repetido no grupo).
        List<IdeiaUsuario> jaVinculado = ideiaUsuarioDAO
                .listarParametro(new IdeiaUsuario(usuario, ideia, null));
        if (jaVinculado != null && !jaVinculado.isEmpty()) {
            // UX: apos participar, leva o usuario direto p/ "Minhas Ideias" (onde a ideia
            // agora aparece), em vez de voltar p/ a listagem de outras ideias.
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        IdeiaUsuario ideiaUsuario = new IdeiaUsuario(usuario, ideia, "N");
        ideiaUsuario.setDtInscricao();
        ideiaUsuarioDAO.salvar(ideiaUsuario);

        response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
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
