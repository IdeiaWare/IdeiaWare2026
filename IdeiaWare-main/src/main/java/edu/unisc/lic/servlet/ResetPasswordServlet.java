package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.EnvioEmail;
import edu.unisc.lic.classes.TokenReset;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/ResetPasswordServlet"})
public class ResetPasswordServlet extends HttpServlet {

    private static final long VALIDADE_TOKEN_MS = 60L * 60 * 1000;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");

            Usuario usuario = new Usuario();
            usuario.setEmail(request.getParameter("email"));

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> lista = usuarioDAO.listarParametro(usuario, false);
            if (lista.size() == 1) {
                usuario = lista.get(0);

                // RESET-TOKEN: gera token de uso unico com expiracao
                String tokenEmClaro = TokenReset.gerar();
                usuario.setResetTokenHash(TokenReset.hash(tokenEmClaro));
                usuario.setResetTokenExpira(new Date(System.currentTimeMillis() + VALIDADE_TOKEN_MS));
                usuarioDAO.editar(usuario);

                String link = baseUrl(request) + "/RedefinirSenhaServlet?token=" + tokenEmClaro;
                String textoEmail = "Olá "+usuario.getNome()+",\n"+
                             "Recebemos uma solicitação para redefinir a senha na ferramenta IdeiaWare.\n\n" +
                             "Clique no link abaixo para escolher uma nova senha (valido por 1 hora):\n\n"+
                             link + "\n\n" +
                             "Se você não solicitou essa alteração, ignore este e-mail -- sua senha continua a mesma.";

                // RET-14-EMAIL: erro de rede cai na mesma resposta anti-enumeracao
                boolean enviado;
                try {
                    enviado = EnvioEmail.EnviaEmail(usuario.getEmail(), "Redefinição de senha - IdeiaWare", textoEmail);
                } catch (IOException ex) {
                    enviado = false;
                }
                if (!enviado) {
                    // SEC-19: resposta nao muda (anti-enumeracao), so loga
                    System.err.println("ResetPasswordServlet: falha ao enviar e-mail de redefinicao para usuario codigo=" + usuario.getCodigo());
                }
            }

            // SEC-19: resposta sempre igual (anti-enumeracao)
            request.setAttribute("SucessoRedefinicaoSenha", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private static String baseUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        boolean portaPadrao = ("http".equals(request.getScheme()) && port == 80)
                || ("https".equals(request.getScheme()) && port == 443);
        return request.getScheme() + "://" + request.getServerName()
                + (portaPadrao ? "" : ":" + port)
                + request.getContextPath();
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
