package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SalvarAudioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // ENCODING-01: sem isso o write de erro sai com acentuacao quebrada (charset default do container).
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String fileData = sb.toString();

        if (fileData == null || fileData.trim().isEmpty()) {
            return;
        }

        // AUDIO-DATAURI: guarda so' a parte base64 pura, sem o prefixo "data:...;base64,".
        int comma = fileData.indexOf(',');
        if (fileData.startsWith("data:") && comma >= 0) {
            fileData = fileData.substring(comma + 1);
        }

        // BLINDA-02: sessao sem storytellingId ou id invalido
        Object storyIdAttr = session.getAttribute("storytellingId");
        if (storyIdAttr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Storytelling st;
        try {
            st = new StorytellingDAO().buscar(Long.parseLong(storyIdAttr.toString()));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (st == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // UX-STORY-ETAPA-TRAVADA: faltava aqui -- os outros 4 servlets ja bloqueavam.
        if (StatusIdeia.FINALIZADO.equals(st.getStatus())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Este item já foi finalizado.");
            return;
        }

        ElementosStorytelling filtro = new ElementosStorytelling();
        filtro.setTipo("AUD");
        filtro.setStorytelling(st);

        List<ElementosStorytelling> estList = new ElementosStorytellingDAO().listarParametro(filtro);

        ElementosStorytelling est = new ElementosStorytelling();
        est.setStorytelling(st);
        est.setCaminho(fileData);
        est.setTipo("AUD");

        // K.8 #7: apaga o antigo e salva o novo numa so transacao
        new ElementosStorytellingDAO().substituirAudio(estList, est);
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
