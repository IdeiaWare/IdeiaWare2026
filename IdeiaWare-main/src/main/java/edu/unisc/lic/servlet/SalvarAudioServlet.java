package edu.unisc.lic.servlet;

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
        HttpSession session = request.getSession(true);

        // Lê o body completo (áudio em base64 pode ser grande)
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

        // BLINDAGEM: sessao sem storytellingId gerava NPE no .toString(); id
        // nao-numerico gerava NumberFormatException -> 500 cru.
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

        // Remove áudio anterior do mesmo storytelling
        ElementosStorytelling filtro = new ElementosStorytelling();
        filtro.setTipo("AUD");
        filtro.setStorytelling(st);

        List<ElementosStorytelling> estList = new ElementosStorytellingDAO().listarParametro(filtro);

        // Novo áudio
        ElementosStorytelling est = new ElementosStorytelling();
        est.setStorytelling(st);
        est.setCaminho(fileData);
        est.setTipo("AUD");

        // K.8 #7: apaga o(s) audio(s) antigo(s) e salva o novo NUMA SO transacao (antes
        // eram excluirTodos() + salvar() em transacoes separadas -- uma falha no meio
        // perdia o audio de vez, sem o antigo nem o novo sobrarem).
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
