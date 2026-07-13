package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.EnvioEmail;
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

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/ResetPasswordServlet"})
public class ResetPasswordServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            Boolean resp = true;
            
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getParameter("email"));
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> lista = usuarioDAO.listarParametro(usuario, false);
            if (lista.size() == 1) {
                usuario = lista.get(0);
                String novaSenha = usuario.ResetaSenha();

                usuarioDAO.editar(usuario);
                String textoEmail = "Olá "+usuario.getNome()+",\n"+
                             "Recebemos uma solicitação para redefinir a senha na ferramenta IdeiaWare.\n\n" +
                             "Por favor, utilize a senha temporária abaixo para acessar sua conta:\n\n"+
                             "Senha Temporária: "+novaSenha+"\n\n" +
                             "IMPORTANTE: Por motivos de segurança, recomendamos que você altere essa senha temporária assim que fizer o login.\n\n"+
                             "Se você não solicitou essa alteração, por favor, entre em contato imediatamente com o administrador.";

                // RET-14-EMAIL: IOException de rede no SendGrid agora cai na mesma resposta anti-enumeracao.
                boolean enviado;
                try {
                    enviado = EnvioEmail.EnviaEmail(usuario.getEmail(), "Redefinição de senha - IdeiaWare", textoEmail);
                } catch (IOException ex) {
                    enviado = false;
                }
                if (!enviado) {
                    // SEC-19: resposta ao usuario nao muda (anti-enumeracao); so loga no servidor.
                    System.err.println("ResetPasswordServlet: falha ao enviar e-mail de redefinicao para usuario codigo=" + usuario.getCodigo());
                }
            }

            // SEC-19/K.1: resposta sempre igual (anti-enumeracao); reset por token/link fica pendente.
            request.setAttribute("SucessoRedefinicaoSenha", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao redefine senha (evita CSRF via GET e acao por link).
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
