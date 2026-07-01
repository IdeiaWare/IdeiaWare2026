/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vinicius Santiago
 */
public class InserirForma extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

        String tipo = data.get("tipo").getAsString();
        double x = data.get("x").getAsDouble();
        double y = data.get("y").getAsDouble();
        
        ElementosStorytelling est = new ElementosStorytelling(st, "IMG", Constantes.CAMINHO_FORMAS + tipo + ".png", x, y, 128, 128);
        // STM-19: salvar() ja preenche o codigo (id) gerado na propria entidade
        // (session.save do Hibernate). O 'ultimoAdicionado' antigo relia o banco e,
        // com varias formas adicionadas rapido, retornava o codigo de OUTRA figura
        // (race) -> duas figuras no canvas com o mesmo id: uma nao apagava e o
        // salvamento embaralhava as posicoes. Agora usamos o proprio 'est'.
        new ElementosStorytellingDAO().salvar(est);

        response.getWriter().write(new Gson().toJson(est));
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
