package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class InserirForma extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);

        HttpSession session = request.getSession();
        // STM-04: evita NPE de unboxing sem storytellingId
        Object storyId = session.getAttribute("storytellingId");
        if (storyId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Storytelling st = new StorytellingDAO().buscar((long) storyId);

        // UX-STORY-ETAPA-TRAVADA: bloqueia escrita se o Storytelling ja foi finalizado (uma
        // aba antiga aberta de outro participante continuava conseguindo inserir formas).
        if (st == null || StatusIdeia.FINALIZADO.equals(st.getStatus())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Este item já foi finalizado.");
            return;
        }

        String tipo = data.get("tipo").getAsString();
        double x = data.get("x").getAsDouble();
        double y = data.get("y").getAsDouble();
        
        ElementosStorytelling est = new ElementosStorytelling(st, "IMG", Constantes.CAMINHO_FORMAS + tipo + ".png", x, y, 128, 128);
        // STM-19: salvar() ja preenche o id em 'est'
        new ElementosStorytellingDAO().salvar(est);

        // GT-01: GSON_SEM_SENHA evita vazar Usuario.senha (fetch EAGER)
        response.getWriter().write(JsonUtil.GSON_SEM_SENHA.toJson(est));
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
