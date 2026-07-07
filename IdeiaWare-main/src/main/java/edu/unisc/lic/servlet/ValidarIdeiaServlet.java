/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author USER
 */
public class ValidarIdeiaServlet extends HttpServlet {

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

        // COL-07: validar ideia é ação de gestor. Sem esta checagem, qualquer
        // usuário logado podia validar/rejeitar ideias chamando o servlet direto.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null || !"adm".equals(sessionUser.getPermissao())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        UsuarioDAO usuDAO = new UsuarioDAO();

        // BLINDAGEM: codigo/codUsuario nulos ou nao-numericos geravam
        // NumberFormatException/NPE (500); ideia inexistente -> NPE no setGestor.
        Ideia ideia;
        Usuario usu;
        try {
            ideia = ideiaDAO.buscar(Long.valueOf(request.getParameter("codigo")));
            usu = usuDAO.buscar(Long.valueOf(request.getParameter("codUsuario")));
        } catch (NumberFormatException | NullPointerException ex) {
            response.sendRedirect(request.getContextPath() + "/validar-ideia.jsp");
            return;
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + "/validar-ideia.jsp");
            return;
        }

        ideia.setGestor(usu);

        String acao = request.getParameter("validar");
        if ("validar".equals(acao) || "reabrir".equals(acao)) {
            // M.1 (2026-07-06): "reabrir" destrava uma ideia REJEITADA -- volta pra
            // VALIDADA (direto pro grupo aberto), mesmo efeito de validar. Antes RE era
            // terminal. Ao reabrir, limpa o motivo de rejeicao (nao faz mais sentido).
            ideia.setStatus(StatusIdeia.VALIDADA);
            ideia.setDtValidacao();
            ideia.setStatusGrupo(StatusIdeia.GRUPO_ABERTO);
            if ("reabrir".equals(acao)) {
                ideia.setMotivoRejeicao(null);
            }
        } else {
            ideia.setStatus(StatusIdeia.REJEITADA);
            ideia.setStatusGrupo(StatusIdeia.GRUPO_FECHADO);
            ideia.setDtRejeicao();
            ideia.setMotivoRejeicao(request.getParameter("motivo"));
        }

        ideiaDAO.editar(ideia);

        response.sendRedirect(request.getContextPath() + File.separator + "validar-ideia.jsp");
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
        // SEC-18: POST-only. GET nao valida/rejeita ideia (evita CSRF via GET).
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
