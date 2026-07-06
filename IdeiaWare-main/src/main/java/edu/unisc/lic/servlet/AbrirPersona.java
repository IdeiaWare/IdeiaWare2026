package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.AbrirPDF;
import edu.unisc.lic.dao.ExportFileDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
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
public class AbrirPersona extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/pdf");

        // AUTORIZACAO: exige login. Antes, qualquer um baixava o PDF de qualquer
        // persona/POV exportado so chutando o id (IDOR / vazamento de dados).
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // RET-14: protege parse + null (fluxo legado pouco usado, mas evita 500).
        String codParam = request.getParameter("codigoPersona");
        ExportFile p = null;
        if (codParam != null) {
            try { p = new ExportFileDAO().buscar(Long.parseLong(codParam)); }
            catch (NumberFormatException e) { p = null; }
        }
        if (p == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // AUTORIZACAO (IDOR): exige que o usuario seja PARTICIPANTE da ideia dona
        // deste export, ou admin -- antes, qualquer usuario logado baixava o PDF de
        // qualquer export so adivinhando o codigo.
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!"adm".equals(sessionUser.getPermissao())) {
            List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                    .listarParametro(new IdeiaUsuario(sessionUser, p.getIdeia(), null));
            if (vinculo == null || vinculo.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        AbrirPDF.abrir(response, p.getFileLocation(), p.getFileName());

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
