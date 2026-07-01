package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.AbrirPDF;
import edu.unisc.lic.dao.ExportFileDAO;
import edu.unisc.lic.domain.ExportFile;
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
public class AbrirPointOfView extends HttpServlet {

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
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");

        // AUTORIZACAO: exige login. Antes, qualquer um baixava o PDF de qualquer
        // persona/POV exportado so chutando o id (IDOR / vazamento de dados).
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // RET-14: protege parse + null (fluxo legado pouco usado, mas evita 500).
        String codParam = request.getParameter("codigoPOV");
        ExportFile pov = null;
        if (codParam != null) {
            try { pov = new ExportFileDAO().buscar(Long.parseLong(codParam)); }
            catch (NumberFormatException e) { pov = null; }
        }
        if (pov == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        AbrirPDF.abrir(response, pov.getFileLocation(), pov.getFileName());
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
