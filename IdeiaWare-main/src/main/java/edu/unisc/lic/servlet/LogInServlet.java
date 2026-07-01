package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.UsuarioDAO;
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
 * @author m78208
 */
public class LogInServlet extends HttpServlet {

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

        request.setCharacterEncoding("UTF-8");

        // SEC-22 (bcrypt): o bcrypt usa salt aleatorio por hash, entao NAO da p/ achar
        // o usuario por "WHERE senha=hash". Busca SO pelo usuario e verifica a senha em
        // claro contra o hash armazenado com checaSenha() (BCrypt.checkpw).
        Usuario filtro = new Usuario();
        filtro.setUsuario(request.getParameter("usuario"));

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> lista = usuarioDAO.listarParametro(filtro, false);

        if (lista.size() == 1 && lista.get(0).checaSenha(request.getParameter("senha"))) {
            Usuario usuario = lista.get(0);

            HttpSession session = request.getSession(true);
            session.setAttribute("codigoUsuario", usuario.getCodigo());
            session.setAttribute("nomeUsuario", usuario.getNome());

            request.getRequestDispatcher("wait.jsp").forward(request, response);
        } else {
            request.setAttribute("resposta", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }

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
        // SEC-18: POST-only. GET nao loga (evita credencial em log/historico e CSRF via GET).
        response.sendRedirect(request.getContextPath() + "/login.jsp");
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
