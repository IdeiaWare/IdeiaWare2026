package edu.unisc.lic.servlet;

import com.google.gson.Gson;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vinicius Santiago
 */
public class RetornaAudio extends HttpServlet {

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
        est.setTipo("AUD");

        List<ElementosStorytelling> lista = new ElementosStorytellingDAO().listarParametro(est);
        String json = new Gson().toJson(lista);

        response.getWriter().write(json);
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
