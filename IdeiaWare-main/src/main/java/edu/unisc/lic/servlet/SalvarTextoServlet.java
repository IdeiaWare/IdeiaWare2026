package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SalvarTextoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // SEC-15: autor vem da sessao, nao de parametro
        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida parametro/entidade antes de gravar
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        try {
            if (ideiaIdParam != null) {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            }
        } catch (NumberFormatException e) {
            ideia = null;
        }
        Usuario autor = new UsuarioDAO().buscar((Long) codigoUsuario);
        if (ideia == null || autor == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");
            return;
        }

        // SRV-IDOR-05: exige lideranca
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(autor, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        LogColaboracao logColaboracao = new LogColaboracao(ideia,
                autor,
                Data.horaAtual(),
                retornaTextoFormatado(request.getParameter("texto"))
        );

        new LogColaboracaoDAO().salvar(logColaboracao);

        response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");
    }

    private String retornaTextoFormatado(String s) {
        if (s == null) {
            return "";
        }
        // STM-02: so recorta se as tags existirem
        int inicio = s.indexOf("<p>");
        if (inicio >= 0) {
            s = s.substring(inicio);
        }
        int fim = s.lastIndexOf("</p>");
        if (fim >= 0) {
            s = s.substring(0, fim);
        }
        s = s.replace("<p>", "");
        s = s.replace("</p>", "<br>");
        return s;
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
        return "Short description";
    }

}
