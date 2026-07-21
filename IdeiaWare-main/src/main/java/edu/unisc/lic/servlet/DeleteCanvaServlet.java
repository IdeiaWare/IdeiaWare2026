package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
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

    // CAN-04/TEST-04: lista fechada de destinos validos
    private static final Set<String> DESTINOS_VALIDOS = new HashSet<>(Arrays.asList(
            "EntrarCanvaServlet", "EntrarCanvaAtividadeServlet", "EntrarCanvaCanalServlet",
            "EntrarCanvaEstruturaServlet", "EntrarCanvaParceriaServlet", "EntrarCanvaPropostaServlet",
            "EntrarCanvaReceitaServlet", "EntrarCanvaRecursoServlet", "EntrarCanvaRelacionamentoServlet",
            "EntrarCanvaSegmentoServlet"));

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(false);

        request.setCharacterEncoding("UTF-8");

        // CANM-09: exige usuario logado e canva ativo na sessao
        if (session == null || session.getAttribute("codigoUsuario") == null
                || session.getAttribute("ideiaId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        CanvaDAO canvaDAO = new CanvaDAO();

        // CANM-04: valida o parametro antes de converter
        String canvaParam = request.getParameter("canva");
        long canvaId;
        try {
            canvaId = Long.parseLong(canvaParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("EntrarCanvaServlet");
            return;
        }

        Canva canva = canvaDAO.buscar(canvaId);

        // CANM-09: so exclui se o post-it pertence a ideia da sessao
        // UX-CANVA-ETAPA-TRAVADA: nao exclui se o Canvas ja foi finalizado (export gerado
        // ficaria desatualizado em relacao aos post-its que ainda podiam ser apagados).
        Long ideiaId = (Long) session.getAttribute("ideiaId");
        if (canva != null && canva.getIdeia() != null && ideiaId.equals(canva.getIdeia().getCodigo())) {
            if (StatusIdeia.FINALIZADO.equals(canva.getIdeia().getStatus())) {
                // UX-PADRAO-ETAPA-FINALIZADA: mensagem via flash de sessao, lida por headerCookies.jsp.
                session.setAttribute("mensagemErroEtapa", "Este Canva já foi finalizado. O post-it não foi excluído.");
                response.sendRedirect("minha-ideia.jsp");
                return;
            }
            canvaDAO.excluir(canva);
        }

        // CAN-04: valida 'context' antes do sendRedirect (open redirect)
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
