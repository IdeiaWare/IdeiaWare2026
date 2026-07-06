package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EntrarStorytellingServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login. Antes o servlet nao checava sessao nenhuma.
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        // RET-14: protege o parse do parametro (evita 500 com valor invalido/nulo).
        Ideia ideia = null;
        try {
            ideia = ideiaDAO.buscar(Long.parseLong(request.getParameter("ideiaId")));
        } catch (NumberFormatException e) {
            ideia = null;
        }

        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // AUTORIZACAO (IDOR): antes o servlet nao checava participacao nenhuma -- a
        // busca de Storytelling filtrava so por ideiaId (usuario=new Usuario(), vazio
        // = "qualquer um"). Qualquer usuario logado trocando ideiaId na URL acessava
        // (e editava, se status DE) o Storytelling de QUALQUER outra ideia.
        Usuario usuarioLogado = new UsuarioDAO().buscar((Long) codigoUsuarioObj);
        List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(usuarioLogado, ideia, null));
        if (vinculo == null || vinculo.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        StorytellingDAO storytellingDAO = new StorytellingDAO();
        List<Storytelling> storytellingList = storytellingDAO.listarParametro(
                new Storytelling(new Usuario(), ideia, null, null));

        // STR-01: verificar se lista está vazia antes de .get(0)
        if (storytellingList == null || storytellingList.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();
        List<LogColaboracao> logList = logColaboracaoDAO.listarParametro(
                new LogColaboracao(ideia, new Usuario(), null, null));

        if (logList == null || logList.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        session.setAttribute("storytellingId", storytellingList.get(0).getCodigo());
        session.setAttribute("ideiaLog", logList.get(0).getCodigo());

        if (StatusIdeia.EM_DESENVOLVIMENTO.equals(storytellingList.get(0).getStatus())) {
            response.sendRedirect(request.getContextPath() + File.separator + "storytelling.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + File.separator + "storytelling-show.jsp");
        }
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
