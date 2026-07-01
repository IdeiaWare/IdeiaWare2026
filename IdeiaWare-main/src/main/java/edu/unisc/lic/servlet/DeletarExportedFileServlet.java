/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.ExportFileDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gustavo Armborst Guedes de Azevedo
 */
public class DeletarExportedFileServlet extends HttpServlet {

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

        // RKM-02: chamado via AJAX. Sem esta checagem, qualquer usuário logado
        // podia deletar qualquer arquivo exportado por id. Só admin pode.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null || !"adm".equals(sessionUser.getPermissao())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String body = request.getReader().readLine();
        if (body == null || body.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long id;
        try {
            id = Long.parseLong(body.trim());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ExportFileDAO exportFileDAO = new ExportFileDAO();
        ExportFile exportFile = exportFileDAO.buscar(id);

        // RKM-02: evita excluir(null) quando o id não existe
        if (exportFile != null) {
            exportFileDAO.excluir(exportFile);
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
