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

public class DeleteCanvaServlet extends HttpServlet {

    // CAN-04/TEST-04: lista fechada de destinos validos (antes bastava comecar com "EntrarCanva").
    private static final Set<String> DESTINOS_VALIDOS = new HashSet<>(Arrays.asList(
            "EntrarCanvaServlet", "EntrarCanvaAtividadeServlet", "EntrarCanvaCanalServlet",
            "EntrarCanvaEstruturaServlet", "EntrarCanvaParceriaServlet", "EntrarCanvaPropostaServlet",
            "EntrarCanvaReceitaServlet", "EntrarCanvaRecursoServlet", "EntrarCanvaRelacionamentoServlet",
            "EntrarCanvaSegmentoServlet"));

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

        // CAN-04: 'context' ia direto pro sendRedirect sem validar (open redirect).
        String context = request.getParameter("context");
        if (!DESTINOS_VALIDOS.contains(context)) {
            context = "EntrarCanvaServlet";
        }
        response.sendRedirect(context);

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
