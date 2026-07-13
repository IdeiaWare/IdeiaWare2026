package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoSalvarStoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // SEC-16: so edita elementos do storytelling ATIVO na sessao (antes, IDOR de escrita).
        javax.servlet.http.HttpSession session = request.getSession(false);
        Object storyId = session == null ? null : session.getAttribute("storytellingId");
        if (storyId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Long storytellingId = (Long) storyId;

        String json = lerBody(request);
        if (json == null || json.trim().isEmpty()) {
            return;
        }

        JsonArray data = new Gson().fromJson(json, JsonArray.class);
        ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();

        // PERF-02: 1 SELECT em lote + 1 Session pra salvar tudo, em vez de 2N conexoes (N=elementos).
        Map<Long, ElementosStorytelling> existentes = buscarExistentes(elementosStorytellingDAO, data);

        List<ElementosStorytelling> paraSalvar = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JsonObject jsonObject = data.get(i).getAsJsonObject();
            ElementosStorytelling est = existentes.get(jsonObject.get("codigo").getAsLong());

            if (podeSalvar(est, storytellingId)) {
                aplicarCampos(est, jsonObject);
                paraSalvar.add(est);
            }
        }

        elementosStorytellingDAO.salvarLote(paraSalvar);
    }

    private String lerBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private Map<Long, ElementosStorytelling> buscarExistentes(
            ElementosStorytellingDAO dao, JsonArray data) {
        List<Long> codigos = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            codigos.add(data.get(i).getAsJsonObject().get("codigo").getAsLong());
        }

        Map<Long, ElementosStorytelling> existentes = new HashMap<>();
        for (ElementosStorytelling est : dao.buscarPorCodigos(codigos)) {
            existentes.put(est.getCodigo(), est);
        }
        return existentes;
    }

    // STR-02/SEC-16: ignora elemento deletado nesse meio-tempo ou de outro storytelling.
    private boolean podeSalvar(ElementosStorytelling est, Long storytellingId) {
        return est != null
                && est.getStorytelling() != null
                && storytellingId.equals(est.getStorytelling().getCodigo());
    }

    private void aplicarCampos(ElementosStorytelling est, JsonObject jsonObject) {
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
