package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Canva;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// MNT-01: classe-base dos 9 servlets "EntrarCanva*" (antes, quase identicos e duplicados).
public abstract class EntrarCanvaBaseServlet extends HttpServlet {

    /** Tipo do elemento (3o arg de listarCanvaElement). Ex.: "atividade". */
    protected abstract String getTipoCanva();

    /** Pagina destino. Ex.: "canva-atividades-principais.jsp". */
    protected abstract String getPaginaDestino();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        request.setCharacterEncoding("UTF-8");

        // CANM-03: guard contra acesso sem ideiaId na sessao.
        if (session.getAttribute("ideiaId") == null) {
            response.sendRedirect(request.getContextPath() + "/lista-canvas.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) session.getAttribute("ideiaId"));
        // SRV-NPE-01: ideiaId na sessao pode apontar pra ideia que nao existe mais.
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + "/lista-canvas.jsp");
            return;
        }

        CanvaDAO canvaDAO = new CanvaDAO();
        Canva canva = new Canva();
        canva.setIdeia(ideia);

        List<Canva> listaCanva = canvaDAO.listarCanvaElement(canva, getTipoCanva());

        session.setAttribute("attributes", listaCanva);
        response.sendRedirect(request.getContextPath() + File.separator + getPaginaDestino());
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
}
