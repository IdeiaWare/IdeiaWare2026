package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;

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
 * @author yanrodrigues
 */
public class VisaoGeralCanvaServlet extends HttpServlet {

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
    	
    	HttpSession session = request.getSession(true);

        request.setCharacterEncoding("UTF-8");
        
        // CAN-09: protege contra ideiaId ausente na sessao. Sem isso, abrir a
        // "Visao Geral" sem um canva ativo chamava buscar(null) -> erro 500
        // "id to load is required for loading".
        Long ideiaId = (Long) request.getSession().getAttribute("ideiaId");
        if (ideiaId == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-canvas.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(ideiaId);
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-canvas.jsp");
            return;
        }

        CanvaDAO canvaDAO = new CanvaDAO();
        Canva canva = new Canva();
        canva.setIdeia(ideia);
        
        List<Canva> receita = canvaDAO.listarCanvaElement(canva, "receita");
        List<Canva> atividade = canvaDAO.listarCanvaElement(canva, "atividade");
        List<Canva> canal = canvaDAO.listarCanvaElement(canva, "canal");
        List<Canva> parceria = canvaDAO.listarCanvaElement(canva, "parceria");
        List<Canva> proposta = canvaDAO.listarCanvaElement(canva, "proposta");
        List<Canva> segmento = canvaDAO.listarCanvaElement(canva, "segmento");
        List<Canva> recurso = canvaDAO.listarCanvaElement(canva, "recurso");
        List<Canva> relacionamento = canvaDAO.listarCanvaElement(canva, "relacionamento");
        List<Canva> custo = canvaDAO.listarCanvaElement(canva, "custo");
        
        session.setAttribute("receita", receita);
        session.setAttribute("atividade", atividade);
        session.setAttribute("canal", canal);
        session.setAttribute("parceria", parceria);
        session.setAttribute("proposta", proposta);
        session.setAttribute("segmento", segmento);
        session.setAttribute("recurso", recurso);
        session.setAttribute("relacionamento", relacionamento);
        session.setAttribute("custo", custo);

        response.sendRedirect(request.getContextPath() + File.separator + "canva-visao-geral.jsp");

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
