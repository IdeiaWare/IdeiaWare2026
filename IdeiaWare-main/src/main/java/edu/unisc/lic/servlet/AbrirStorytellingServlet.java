package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.AbrirPDF;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// PDF-DISCO
public class AbrirStorytellingServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");

        // SEC-17: exige login.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String codParam = request.getParameter("id");
        Storytelling storytelling = null;
        if (codParam != null) {
            try { storytelling = new StorytellingDAO().buscar(Long.parseLong(codParam)); }
            catch (NumberFormatException e) { storytelling = null; }
        }
        if (storytelling == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // SRV-IDOR-07: exige participacao na ideia dona do export, ou admin.
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!"adm".equals(sessionUser.getPermissao())) {
            List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                    .listarParametro(new IdeiaUsuario(sessionUser, storytelling.getIdeia(), null));
            if (vinculo == null || vinculo.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        AbrirPDF.abrir(response, storytelling.getCaminhoFinalizado(), "storytelling");
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
        return "Abre o PDF de um Storytelling finalizado.";
    }

}
