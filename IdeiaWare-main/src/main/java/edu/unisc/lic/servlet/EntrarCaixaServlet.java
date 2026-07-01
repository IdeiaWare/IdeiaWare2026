/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.AssinaturaCaixa;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Gustavo Armborst Guedes de Azevedo
 */
public class EntrarCaixaServlet extends HttpServlet {

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

        // SEC-23: exige login + que o usuario seja PARTICIPANTE da ideia, e ASSINA o
        // ideiaId (HMAC). O Toolkit so aceita um ideiaId assinado aqui -> o cookie deixa
        // de ser forjavel (antes qualquer um setava ideiaId=N na mao e entrava na Caixa).
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Long ideiaId;
        try {
            ideiaId = Long.parseLong(request.getParameter("ideiaId"));
        } catch (NumberFormatException e) {
            ideiaId = null;
        }
        if (ideiaId == null) {
            response.sendRedirect(request.getContextPath() + "/lista-caixa-de-ferramentas.jsp");
            return;
        }

        // O usuario logado participa desta ideia?
        Usuario u = new Usuario();
        u.setCodigo((Long) session.getAttribute("codigoUsuario"));
        Ideia i = new Ideia();
        i.setCodigo(ideiaId);
        List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(u, i, null));
        if (vinculo == null || vinculo.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/lista-caixa-de-ferramentas.jsp");
            return;
        }

        // Host atual da requisição (localhost em dev, spi.unisc.br em prod).
        String host = request.getServerName();
        int port = request.getServerPort();

        // Cookies sem dominio explicito -> valem para o host atual; lidos pelo Toolkit
        // (mesmo host). setPath("/") cobre /LIC e /toolkit.
        Cookie ck = new Cookie("ideiaId", String.valueOf(ideiaId));
        ck.setMaxAge(-1);
        ck.setPath("/");
        response.addCookie(ck);

        // Assinatura HMAC do ideiaId, validada pelo Toolkit (AdminCookies).
        Cookie ckSig = new Cookie("ideiaSig", AssinaturaCaixa.assinar(String.valueOf(ideiaId)));
        ckSig.setMaxAge(-1);
        ckSig.setPath("/");
        response.addCookie(ckSig);

        String nomeParam = request.getParameter("usuarioNome");
        Cookie ck2 = new Cookie("usuarioNome", URLEncoder.encode(nomeParam == null ? "" : nomeParam, "UTF-8"));
        ck2.setMaxAge(-1);
        ck2.setPath("/");
        response.addCookie(ck2);

        // INFRA-11: usa o scheme da requisicao (http em dev, https em prod).
        response.sendRedirect(request.getScheme() + "://" + host + ":" + port + "/toolkit");
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
