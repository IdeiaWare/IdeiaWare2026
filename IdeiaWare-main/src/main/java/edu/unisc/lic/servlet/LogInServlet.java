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

public class LogInServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // SEC-22: busca so por usuario, verifica senha com checaSenha()
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only
        response.sendRedirect(request.getContextPath() + "/login.jsp");
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
