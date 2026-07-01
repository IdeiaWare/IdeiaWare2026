package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoSalvarStoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: so edita elementos do storytelling ATIVO na sessao. Sem isto,
        // dava p/ alterar (mover/editar) elementos de QUALQUER storytelling passando
        // o codigo no JSON (IDOR de escrita).
        javax.servlet.http.HttpSession session = request.getSession(false);
        Object storyId = session == null ? null : session.getAttribute("storytellingId");
        if (storyId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Long storytellingId = (Long) storyId;

        // Lê o body completo (não apenas a primeira linha)
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        if (json == null || json.trim().isEmpty()) {
            return;
        }

        JsonArray data = new Gson().fromJson(json, JsonArray.class);
        ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();

        for (int i = 0; i < data.size(); i++) {
            JsonObject jsonObject = data.get(i).getAsJsonObject();
            ElementosStorytelling est = elementosStorytellingDAO.buscar(
                    jsonObject.get("codigo").getAsLong());

            // STR-02: elemento pode ter sido deletado entre o carregamento e o save
            if (est == null) {
                continue;
            }

            // IDOR: ignora elementos que nao pertencem ao storytelling da sessao.
            if (est.getStorytelling() == null
                    || !storytellingId.equals(est.getStorytelling().getCodigo())) {
                continue;
            }

            if ("texto".equals(jsonObject.get("tipo").getAsString())) {
                est.setY(jsonObject.get("y").getAsDouble());
                est.setX(jsonObject.get("x").getAsDouble());
                est.setInformacaoTexto(jsonObject.get("conteudo").getAsString());
                est.setTamanhoFonte(jsonObject.get("tamanhoFonte").getAsInt());
                est.setFonte(jsonObject.get("fonte").getAsString());
                est.setCorFonte(jsonObject.get("cor").getAsString());
            } else {
                est.setAltura(jsonObject.get("height").getAsDouble());
                est.setLargura(jsonObject.get("width").getAsDouble());
                est.setY(jsonObject.get("y").getAsDouble());
                est.setX(jsonObject.get("x").getAsDouble());
            }

            elementosStorytellingDAO.editar(est);
        }
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
