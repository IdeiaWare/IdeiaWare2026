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

public class AbrirPersona extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/pdf");

        // SEC-17: exige login (antes, PDF exportado abria sem auth pra quem chutasse o id).
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

        // SRV-IDOR-07: exige participacao na ideia dona do export, ou admin (antes, IDOR).
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
