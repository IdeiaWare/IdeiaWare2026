/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author yanrodrigues
 */
public class DeleteCanvaServlet extends HttpServlet {

    // TEST-04: lista fechada dos destinos validos -- antes bastava comecar com
    // "EntrarCanva" (ex.: "EntrarCanvaXxx", que nao existe, passava na checagem e so
    // quebrava em runtime com 404). Nao era um open-redirect explorado, mas uma lista
    // fechada fecha o buraco por completo em vez de confiar num prefixo de string.
    private static final Set<String> DESTINOS_VALIDOS = new HashSet<>(Arrays.asList(
            "EntrarCanvaServlet", "EntrarCanvaAtividadeServlet", "EntrarCanvaCanalServlet",
            "EntrarCanvaEstruturaServlet", "EntrarCanvaParceriaServlet", "EntrarCanvaPropostaServlet",
            "EntrarCanvaReceitaServlet", "EntrarCanvaRecursoServlet", "EntrarCanvaRelacionamentoServlet",
            "EntrarCanvaSegmentoServlet"));

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
    	
    	HttpSession session = request.getSession(false);

        request.setCharacterEncoding("UTF-8");

        // CANM-09: exige usuário logado e um canva ativo na sessão.
        if (session == null || session.getAttribute("codigoUsuario") == null
                || session.getAttribute("ideiaId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        CanvaDAO canvaDAO = new CanvaDAO();

        // CANM-04: valida o parâmetro antes de converter
        String canvaParam = request.getParameter("canva");
        long canvaId;
        try {
            canvaId = Long.parseLong(canvaParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("EntrarCanvaServlet");
            return;
        }

        Canva canva = canvaDAO.buscar(canvaId);

        // CANM-09 (IDOR): só exclui se o post-it pertence à ideia da sessão.
        Long ideiaId = (Long) session.getAttribute("ideiaId");
        if (canva != null && canva.getIdeia() != null
                && ideiaId.equals(canva.getIdeia().getCodigo())) {
            canvaDAO.excluir(canva);
        }

        // CAN-04: 'context' vinha do formulário direto para o sendRedirect,
        // permitindo redirecionamento para sites externos (open redirect).
        // TEST-04: trocado o prefixo "EntrarCanva" por uma lista fechada dos
        // servlets de destino realmente validos.
        String context = request.getParameter("context");
        if (!DESTINOS_VALIDOS.contains(context)) {
            context = "EntrarCanvaServlet";
        }
        response.sendRedirect(context);

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
