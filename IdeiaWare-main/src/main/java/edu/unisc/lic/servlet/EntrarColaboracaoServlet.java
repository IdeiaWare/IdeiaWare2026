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
public class EntrarColaboracaoServlet extends HttpServlet {

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

        // AUTORIZACAO: exige login. Antes o servlet nao checava sessao nenhuma --
        // um acesso sem login dava NPE no unboxing de codigoUsuario (Long -> long).
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida parametro/ideia antes de usar (evita 500/NPE). Tambem
        // removido o System.out.println de debug.
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

        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario u = uDAO.buscar((Long) codigoUsuarioObj);

        IdeiaUsuarioDAO iuDAO = new IdeiaUsuarioDAO();
        IdeiaUsuario iu = new IdeiaUsuario();
        iu.setIdeia(ideia);
        iu.setUsuario(u);

        // COL-08: protege o .get(0) quando o usuário ainda não tem vínculo com a
        // ideia (lista vazia), em vez de estourar IndexOutOfBounds.
        List<IdeiaUsuario> lista = iuDAO.listarParametro(iu);
        if (lista.size() > 0) {
        	iu = lista.get(0);
        } else {
        	iu = null;
        }

        // AUTORIZACAO (IDOR): antes o servlet calculava "iu" so pra saber o flLider,
        // mas NUNCA barrava o acesso com isso -- qualquer usuario logado trocando
        // ideiaId na URL entrava na colaboracao ativa de QUALQUER outra pessoa (e
        // conseguia ler/escrever, ja que EnviarColaboracaoServlet/AddDescricaoServlet/
        // RetornaMensagensServlet confiam no ideiaId da sessao sem checar de novo).
        // Admin mantem acesso irrestrito -- gerenciamento-ideia.jsp usa esta mesma
        // tela (retencao=true) pra visualizar a colaboracao de qualquer ideia.
        //
        // REVISAO 2026-07-07: essa checagem so testava "iu == null", mas com o M.2
        // (lista de espera) o vinculo PODE existir com flStatusVinculo=PENDENTE ou
        // REJEITADO -- ou seja, um usuario que so PEDIU pra entrar (ainda nao
        // aprovado pelo lider), ou que foi explicitamente rejeitado, tinha "iu != null"
        // e passava direto, furando a lista de espera inteira via POST direto nesta
        // URL. So aprovado (ou vinculo legado, flStatusVinculo == null) tem acesso.
        boolean vinculoAprovado = iu != null && (iu.getFlStatusVinculo() == null
                || edu.unisc.lic.classes.StatusIdeia.VINCULO_APROVADO.equals(iu.getFlStatusVinculo()));
        if (u == null || (!vinculoAprovado && !"adm".equals(u.getPermissao()))) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        String retencao = request.getParameter("retencao");
        if (retencao == null || retencao.isEmpty()) {
            session.setAttribute("isRetencao", false);
        } else {
            session.setAttribute("isRetencao", retencao);
        }

        session.setAttribute("ideiaId", ideia.getCodigo());
        session.setAttribute("ideiaTitulo", ideia.getTitulo());
        session.setAttribute("ideiaDesc", ideia.getDescricao());
        session.setAttribute("lider", iu != null ? iu.getFlLider() : "N");

        response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");

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
