package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
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

public class EnviarCanvaServlet extends HttpServlet {

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

        // UX-CANVA-ETAPA-TRAVADA: bloqueia escrita se o Canvas ja foi finalizado (o export
        // gerado ficaria desatualizado em relacao aos post-its, ninguem checava isso antes).
        // UX-PADRAO-ETAPA-FINALIZADA: mensagem via flash de sessao, lida por headerCookies.jsp.
        if (ideia == null || StatusIdeia.FINALIZADO.equals(ideia.getStatus())) {
            session.setAttribute("mensagemErroEtapa", "Este Canva já foi finalizado. Suas alterações não foram salvas.");
            response.sendRedirect("minha-ideia.jsp");
            return;
        }

        // UX-CANVA-VALIDACAO-SERVIDOR: texto vazio/curto ja era barrado no client
        // (minlength=5), mas nao no servidor -- um POST direto passava sem checagem nenhuma.
        String text = request.getParameter("text");
        if (text == null || text.trim().length() < 5) {
            response.sendRedirect("EntrarCanvaServlet");
            return;
        }

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
            // CANM-02: so edita post-it que existe e pertence a ideia
            if (canva == null || canva.getIdeia() == null
                    || !canva.getIdeia().getCodigo().equals((Long) session.getAttribute("ideiaId"))) {
                response.sendRedirect("EntrarCanvaServlet");
                return;
            }
            canva.setText(text);
            canva.setColor(request.getParameter("color"));
            canva.setAttribute(attribute);
            canvaDAO.editar(canva);
        } else {
            canva = new Canva(ideia, text, request.getParameter("color"), attribute);
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
