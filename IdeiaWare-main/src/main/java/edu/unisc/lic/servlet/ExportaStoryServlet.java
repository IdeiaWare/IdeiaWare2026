package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ExportaStoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);

        // Lê o body completo — o base64 do PDF é uma string grande,
        // então acumulamos tudo (não só a primeira linha).
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String fileData = sb.toString();

        if (fileData == null || fileData.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // BLINDAGEM: sessao sem storytellingId (expirou/acesso direto) gerava NPE no
        // .toString(); id nao-numerico gerava NumberFormatException -> 500 cru.
        Object storyIdAttr = session.getAttribute("storytellingId");
        if (storyIdAttr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        StorytellingDAO storytellingDAO = new StorytellingDAO();
        Storytelling storytelling;
        try {
            storytelling = storytellingDAO.buscar(Long.parseLong(storyIdAttr.toString()));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (storytelling == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        storytelling.setCaminhoFinalizado(fileData);
        storytelling.setDtFinalizacao();
        storytelling.setStatus(StatusIdeia.FINALIZADO);
        storytellingDAO.editar(storytelling);

        Ideia ideia = storytelling.getIdeia();
        ideia.setStatus(StatusIdeia.CAIXA_FERRAMENTAS);
        new IdeiaDAO().editar(ideia);
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
