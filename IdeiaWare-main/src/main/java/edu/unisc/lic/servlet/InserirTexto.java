package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class InserirTexto extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);

        HttpSession session = request.getSession();
        // STM-04: evita NPE de unboxing se a sessão não tiver storytellingId.
        Object storyId = session.getAttribute("storytellingId");
        if (storyId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Storytelling st = new StorytellingDAO().buscar((long) storyId);

        ElementosStorytelling est = new ElementosStorytelling();
        est.setStorytelling(st);
        est.setX(data.get("x").getAsDouble());
        est.setY(data.get("y").getAsDouble());
        est.setFonte(data.get("fonte").getAsString());
        est.setTamanhoFonte(data.get("tamanho").getAsInt());
        est.setCorFonte(data.get("cor").getAsString());
        est.setInformacaoTexto(data.get("informacaoTexto").getAsString());
        est.setCaminho("texto");
        est.setTipo("TXT");
        
        // STM-19: salvar() ja preenche o id em 'est' (o antigo ultimoAdicionado() tinha race).
        new ElementosStorytellingDAO().salvar(est);

        response.getWriter().write(new Gson().toJson(est));
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
