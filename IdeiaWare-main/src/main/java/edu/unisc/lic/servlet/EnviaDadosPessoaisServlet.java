package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.EnvioEmail;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "EnviaDadosPessoaisServlet", urlPatterns = {"/EnviaDadosPessoaisServlet"})
public class EnviaDadosPessoaisServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            HttpSession session = request.getSession(true);

            // RET-14: exige login
            Object codigoObj = session.getAttribute("codigoUsuario");
            if (codigoObj == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscar((Long) codigoObj);
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            String textoEmail = "Olá "+usuario.getNome()+",\n"+
                             "Recebemos sua solicitação para obter seus dados pessoais na ferramenta IdeiaWare.\n\n" +
                             "Segue dados pessoais que temos da sua conta:\n"+
                             usuario.getDadosPessoais()+"\n\n" +
                             "Se você não solicitou essas informação, por favor, entre em contato imediatamente com o administrador.";
            
            // RET-14-EMAIL: erro de rede cai no fluxo de erro amigavel
            Boolean resp;
            try {
                resp = EnvioEmail.EnviaEmail(usuario.getEmail(), "Dados pessoais - IdeiaWare", textoEmail);
            } catch (IOException ex) {
                resp = false;
            }

            if (resp)
                request.setAttribute("EnviouEmail", true);
            else
                request.setAttribute("ErroEnvioEmail", true);
            
            request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
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
