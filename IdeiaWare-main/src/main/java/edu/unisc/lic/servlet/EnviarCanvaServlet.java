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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author yanrodrigues
 */
public class EnviarCanvaServlet extends HttpServlet {

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

        // CANM-01: o switch é sobre 'attribute'; null causaria NullPointerException.
        String attribute = request.getParameter("attribute");
        if (attribute == null || attribute.trim().isEmpty()) {
            response.sendRedirect("EntrarCanvaServlet");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) session.getAttribute("ideiaId"));

        CanvaDAO canvaDAO = new CanvaDAO();

        Canva canva;

        // CAN-02: idCanva vazio significa NOVO post-it.
        String idCanva = request.getParameter("idCanva");
        if (idCanva != null && !idCanva.trim().isEmpty()) {
            Long canvaId;
            try {
                canvaId = Long.parseLong(idCanva);
            } catch (NumberFormatException e) {
                response.sendRedirect("EntrarCanvaServlet");
                return;
            }
            canva = canvaDAO.buscar(canvaId);
            // CANM-02: só edita se o post-it ainda existe e pertence à ideia da sessão (IDOR)
            if (canva == null || canva.getIdeia() == null
                    || !canva.getIdeia().getCodigo().equals((Long) session.getAttribute("ideiaId"))) {
                response.sendRedirect("EntrarCanvaServlet");
                return;
            }
            canva.setText(request.getParameter("text"));
            canva.setColor(request.getParameter("color"));
            canva.setAttribute(attribute);
            canvaDAO.editar(canva);
        } else {
            canva = new Canva(ideia, request.getParameter("text"), request.getParameter("color"), attribute);
            canvaDAO.salvar(canva);
        }

		String caminho = "index.jsp";

		switch (attribute) {
			case "receita":
				caminho = "EntrarCanvaReceitaServlet";
				break;
			case "atividade":
				caminho = "EntrarCanvaAtividadeServlet";
				break;
			case "canal":
				caminho = "EntrarCanvaCanalServlet";
				break;
			case "custo":
				caminho = "EntrarCanvaEstruturaServlet";
				break;
			case "parceria":
				caminho = "EntrarCanvaParceriaServlet";
				break;
			case "proposta":
				caminho = "EntrarCanvaPropostaServlet";
				break;
			case "recurso":
				caminho = "EntrarCanvaRecursoServlet";
				break;
			case "relacionamento":
				caminho = "EntrarCanvaRelacionamentoServlet";
				break;
			case "segmento":
				caminho = "EntrarCanvaSegmentoServlet";
				break;
		}
		
        response.sendRedirect(caminho);
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
