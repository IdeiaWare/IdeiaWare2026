package edu.unisc.lic.servlet;

import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.dao.UsuarioDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.exception.ConstraintViolationException;

public class CadastroUsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getParameter("usuario"));

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> lista = usuarioDAO.listarParametro(usuario, false);
        
        // INFRA-07: "".equals(...) e null-safe (POST sem algum campo dava NPE antes).
        if ("".equals(request.getParameter("nome")) || "".equals(request.getParameter("senha")) ||
            "".equals(request.getParameter("usuario")) || "".equals(request.getParameter("senha2")) ||
            "".equals(request.getParameter("email")) ||
            request.getParameter("nome") == null || request.getParameter("senha") == null ||
            request.getParameter("usuario") == null || request.getParameter("senha2") == null ||
            request.getParameter("email") == null) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro3", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
            
        if (!request.getParameter("senha").equals(request.getParameter("senha2"))){
            Boolean resp = true;

            request.setAttribute("respostaCadastro2", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
        
        Usuario usuarioE = new Usuario();
        usuarioE.setEmail(request.getParameter("email"));
        List<Usuario> listaE = usuarioDAO.listarParametro(usuarioE, false);
        
        if (listaE.size() == 1) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro4", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }      
        
        if (lista.size() == 1) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
        
        usuario.setNome(request.getParameter("nome"));
        usuario.setSenha(request.getParameter("senha"),true);
        usuario.setPermissao("col");
        usuario.setEmail(request.getParameter("email"));
        usuario.setAnonimizado("N");

        // RACE-01: checagens acima tem janela de corrida; a UNIQUE do banco e a trava real.
        try {
            usuarioDAO.salvar(usuario);
        } catch (ConstraintViolationException ex) {
            request.setAttribute("respostaCadastro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("LogInServlet").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao cadastra (evita CSRF via GET).
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
