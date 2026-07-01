/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.LogColaboracao;

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
public class EditarTextoServlet extends HttpServlet {

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

        // RET-14: valida parametro/ideia antes de usar (evita 500/NPE).
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        if (ideiaIdParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("ideiaId", ideia.getCodigo());
        session.setAttribute("ideiaTitulo", ideia.getTitulo());

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();
//        ColaboracaoIdeia ci = new ColaboracaoIdeia(ideia, null, null, null);

//        List<ColaboracaoIdeia> li = colaboracaoIdeiaDAO.mensagensSalvadas(ideia);
//        String aws = "<p>" + ideia.getDescricao() + "</p>";
//
//        for (ColaboracaoIdeia colaboracaoIdeia : li) {
//            aws += "<p>" + colaboracaoIdeia.getDescricaoIdeiaAtual() + "</p>";
//        }

        LogColaboracao lc = new LogColaboracao();
        lc.setIdeia(ideia);

        // STM-01: buscarDescricaoFinal pode retornar null (nenhum LogColaboracao).
        // Usa a descrição da ideia como fallback em vez de causar NPE.
        LogColaboracao descFinal = new LogColaboracaoDAO().buscarDescricaoFinal(lc);
        String descricao;
        if (descFinal != null && descFinal.getDescricao() != null) {
            descricao = descFinal.getDescricao();
        } else if (ideia.getDescricao() != null) {
            descricao = ideia.getDescricao();
        } else {
            descricao = "";
        }

        session.setAttribute("ideiaDescricao", "<p>" + descricao + "</p>");

        response.sendRedirect(request.getContextPath() + File.separator + "editar-texto.jsp");
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
