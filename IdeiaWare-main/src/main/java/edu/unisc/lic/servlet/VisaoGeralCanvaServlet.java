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

public class VisaoGeralCanvaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);

        request.setCharacterEncoding("UTF-8");
        
        // CAN-09: protege contra ideiaId ausente na sessao (senao, buscar(null) dava 500).
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
