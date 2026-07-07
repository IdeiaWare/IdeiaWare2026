/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author USER
 */
public class EnviarColaboracaoServlet extends HttpServlet {

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

        // COLM-03: exige sessão válida com ideiaId e usuário logado.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ideiaId") == null
                || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) session.getAttribute("ideiaId"));

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

        ColaboracaoIdeia colaboracaoIdeia = new ColaboracaoIdeia(ideia, usuario, Data.horaAtual(), request.getParameter("descricao"));

        colaboracaoIdeiaDAO.salvar(colaboracaoIdeia);

        // COLM-02: após salvar, o próprio objeto já tem o código gerado pelo
        // Hibernate. Retorná-lo direto elimina as consultas extras e o
        // get(size-1) sujeito a race condition que havia antes.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // REVISAO 2026-07-07: JsonUtil.GSON_SEM_SENHA (nao new Gson()) -- colaboracaoIdeia
        // carrega .usuario, que tem o hash bcrypt da senha; Gson padrao serializa TODOS os
        // campos por reflection e vazaria o hash pro proprio autor da colaboracao.
        response.getWriter().write(JsonUtil.GSON_SEM_SENHA.toJson(colaboracaoIdeia));
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
