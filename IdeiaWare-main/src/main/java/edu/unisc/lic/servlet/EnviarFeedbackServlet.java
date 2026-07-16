package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.EnvioEmail;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// FUT-02: botao de feedback/sugestao na home -- manda por e-mail pro destinatario configurado
// em FEEDBACK_EMAIL. NUNCA usar o mesmo endereco do SENDGRID_FROM_EMAIL aqui -- from==to em
// provedores como outlook.com cai no filtro anti-spoofing e o envio e descartado em silencio.
@WebServlet(name = "EnviarFeedbackServlet", urlPatterns = {"/EnviarFeedbackServlet"})
public class EnviarFeedbackServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String sugestao = request.getParameter("sugestao");
        if (sugestao == null || sugestao.trim().isEmpty()) {
            request.setAttribute("feedbackErro", true);
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        Usuario usuario = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        String destinatario = System.getenv("FEEDBACK_EMAIL");

        boolean enviado = false;
        if (destinatario != null && !destinatario.isEmpty()) {
            String texto = "Nova sugestao recebida no IdeiaWare.\n\n"
                    + "De: " + usuario.getNome() + " (" + usuario.getUsuario() + ")\n"
                    + "E-mail: " + usuario.getEmail() + "\n\n"
                    + "Sugestao:\n" + sugestao;
            try {
                enviado = EnvioEmail.EnviaEmail(destinatario, "Nova sugestao - IdeiaWare", texto);
            } catch (IOException ex) {
                enviado = false;
            }
        } else {
            System.err.println("EnviarFeedbackServlet: FEEDBACK_EMAIL nao configurado no ambiente.");
        }

        request.setAttribute(enviado ? "feedbackEnviado" : "feedbackErro", true);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao envia feedback (evita CSRF via GET).
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Envia sugestao/feedback do usuario logado por e-mail.";
    }
}
