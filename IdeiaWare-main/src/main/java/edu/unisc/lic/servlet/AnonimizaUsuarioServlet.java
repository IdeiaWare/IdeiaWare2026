package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AnonimizaUsuarioServlet", urlPatterns = {"/AnonimizaUsuarioServlet"})
public class AnonimizaUsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            HttpSession session = request.getSession(true);
            
            Usuario usuario = new Usuario();
            usuario.setCodigo((Long) session.getAttribute("codigoUsuario"));

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

            // RKM-04: valida a senha antes de usa-la
            String senhaAtual = request.getParameter("senhaAtual");
            if (senhaAtual == null || senhaAtual.isEmpty()) {
                request.setAttribute("respostaSenhaInvalida", true);
                request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
                return;
            }

            // SEC-22: verifica a senha atual com bcrypt
            if (!usuario.checaSenha(senhaAtual)) {
                request.setAttribute("respostaSenhaInvalida", true);
                request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                return;
            }
            
            usuario.AnonimizaDadosPessoais();
            usuarioDAO.editar(usuario);
            session.invalidate();
        
            request.getRequestDispatcher("login.jsp").forward(request, response); 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only, GET nao anonimiza
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
