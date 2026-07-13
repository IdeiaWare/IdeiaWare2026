package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeletarObjServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // STM-06: exige usuário logado e um storytelling ativo na sessão.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null
                || session.getAttribute("storytellingId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String json = request.getReader().readLine();
        if (json == null || json.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long id;
        try {
            JsonPrimitive data = new Gson().fromJson(json, JsonPrimitive.class);
            id = data.getAsLong();
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();
        ElementosStorytelling elementosStorytelling = elementosStorytellingDAO.buscar(id);

        // STM-05: não tenta excluir se o elemento não existe.
        if (elementosStorytelling == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // STM-06: so exclui elemento do storytelling ativo na sessao (antes, IDOR).
        Long storyId = (Long) session.getAttribute("storytellingId");
        if (elementosStorytelling.getStorytelling() == null
                || !storyId.equals(elementosStorytelling.getStorytelling().getCodigo())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        elementosStorytellingDAO.excluir(elementosStorytelling);
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
