/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
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
public class SalvarTextoServlet extends HttpServlet {

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

        // AUTORIZACAO: o autor do texto e o USUARIO LOGADO (sessao), NAO um parametro.
        // Antes o autor vinha de request.getParameter("idUsuario") -> dava p/ gravar
        // uma colaboracao no NOME de outro usuario (falsificacao de identidade).
        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida parametro/entidade antes de gravar (evita 500/NPE).
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        try {
            if (ideiaIdParam != null) {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            }
        } catch (NumberFormatException e) {
            ideia = null;
        }
        Usuario autor = new UsuarioDAO().buscar((Long) codigoUsuario);
        if (ideia == null || autor == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");
            return;
        }

        // AUTORIZACAO: so o LIDER da ideia pode editar o texto oficial -- essa
        // restricao so existia na UI (colaboracao.jsp escondia o botao "Editar Texto"
        // pra quem nao era lider); o servlet aceitava de qualquer usuario logado,
        // mesmo sem vinculo com a ideia informada no parametro.
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(autor, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        LogColaboracao logColaboracao = new LogColaboracao(ideia,
                autor,
                Data.horaAtual(),
                retornaTextoFormatado(request.getParameter("texto"))
        );

        new LogColaboracaoDAO().salvar(logColaboracao);

        response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");
    }

    private String retornaTextoFormatado(String s) {
        if (s == null) {
            return "";
        }
        // STM-02: só recorta se as tags existirem; senão substring(-1) estouraria.
        int inicio = s.indexOf("<p>");
        if (inicio >= 0) {
            s = s.substring(inicio);
        }
        int fim = s.lastIndexOf("</p>");
        if (fim >= 0) {
            s = s.substring(0, fim);
        }
        s = s.replace("<p>", "");
        s = s.replace("</p>", "<br>");
        return s;
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
