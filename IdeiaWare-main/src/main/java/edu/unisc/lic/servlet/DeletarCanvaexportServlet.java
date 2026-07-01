package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.CanvaexportDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Exclui um Canva exportado (Canvaexport) a partir da Retencao do Conhecimento.
 *
 * Espelha o DeletarExportedFileServlet (que so trata ExportFile de persona/POV):
 * a Retencao e admin-only, entao exige login + permissao 'adm' antes de excluir,
 * com guard de parse e null-check. Chamado via AJAX (POST) pela funcao
 * deleteCanvaExport() do master.js -> coberto pelo CsrfFilter (token no header).
 */
public class DeletarCanvaexportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Auth: so admin pode excluir (mesma regra do DeletarExportedFileServlet / RKM-02).
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null || !"adm".equals(sessionUser.getPermissao())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String body = request.getReader().readLine();
        if (body == null || body.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long codigo;
        try {
            codigo = Long.parseLong(body.trim());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        CanvaexportDAO canvaexportDAO = new CanvaexportDAO();
        Canvaexport canvaexport = canvaexportDAO.buscar(codigo);

        // Evita excluir(null) quando o codigo nao existe.
        if (canvaexport != null) {
            canvaexportDAO.excluir(canvaexport);
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
        return "Exclui um Canva exportado (Canvaexport) na Retencao.";
    }
}
