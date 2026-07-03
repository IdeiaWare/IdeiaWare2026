package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

import java.io.File;
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
public class GerenciarIdeiaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);

        // RETENCAO-ACESSO: exige login. Antes este servlet nao checava sessao
        // nenhuma (a checagem de permissao ficava so na JSP de destino).
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }
        Usuario usuario = new UsuarioDAO().buscar((Long) codigoUsuarioObj);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida o parametro e a existencia da ideia antes de usar. Sem
        // isso, ideiaId nulo/invalido dava 500 (NumberFormatException) e uma ideia
        // ja removida dava NPE em ideia.getCodigo().
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        if (ideiaIdParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia-gerenciamento.jsp");
            return;
        }

        // RETENCAO-ACESSO (IDOR): agora que a tela e liberada p/ todos, admin ve
        // qualquer ideia mas colaborador so ve as que participa -- sem isso, dava
        // p/ trocar o ideiaId na URL e ver o historico de uma ideia de outro usuario.
        if (!"adm".equals(usuario.getPermissao())) {
            List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                    .listarParametro(new IdeiaUsuario(usuario, ideia, null));
            if (vinculo == null || vinculo.isEmpty()) {
                response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia-gerenciamento.jsp");
                return;
            }
        }

        session.setAttribute("ideiaId", ideia.getCodigo());

        response.sendRedirect(request.getContextPath() + File.separator + "gerenciamento-ideia.jsp");

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
