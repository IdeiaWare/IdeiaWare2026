/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gustavo Armborst Guedes de Azevedo
 */
public class RetornaMensagensServlet extends HttpServlet {

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
        response.setCharacterEncoding("UTF-8");

        // COLM-05: sem ideiaId na sessão não há o que retornar.
        Object ideiaIdObj = request.getSession().getAttribute("ideiaId");
        if (ideiaIdObj == null) {
            response.setContentType("text/plain");
            response.getWriter().write("não");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) ideiaIdObj);

        // BLINDAGEM: ideiaId na sessao pode apontar p/ uma ideia que nao existe mais
        // (hoje inatingivel, ja que nao ha "excluir ideia", mas evita NPE se essa feature
        // existir no futuro -- sem isso, colaboracaoIdeia.getIdeia().getCodigo() no DAO
        // estourava NPE em vez de cair no mesmo fallback "não" usado abaixo).
        if (ideia == null) {
            response.setContentType("text/plain");
            response.getWriter().write("não");
            return;
        }

        // COLM-04: o polling roda a cada 2s; um valor inválido não deve gerar 500.
        int numMensagens;
        try {
            numMensagens = Integer.parseInt(request.getParameter("numMensagens"));
        } catch (NumberFormatException e) {
            response.setContentType("text/plain");
            response.getWriter().write("não");
            return;
        }

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

        ColaboracaoIdeia colaboracaoIdeia = new ColaboracaoIdeia();

        colaboracaoIdeia.setIdeia(ideia);

        List<ColaboracaoIdeia> lista = colaboracaoIdeiaDAO.listarParametro(colaboracaoIdeia);

        int teste = lista.size();

//        System.out.println("\n\n\n\n " + request.getParameter("numMensagens") + "  " + teste + "\n\n\n\n\n\n\n\n\n");
        if (numMensagens < teste) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(colaboracaoIdeiaDAO.ultimaColab(colaboracaoIdeia)));

        } else {
            response.setContentType("text/plain");
            response.getWriter().write("não");
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
