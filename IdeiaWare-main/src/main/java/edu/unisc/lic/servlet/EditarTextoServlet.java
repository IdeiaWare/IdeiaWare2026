package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
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

public class EditarTextoServlet extends HttpServlet {

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

        // RET-14: valida parametro/ideia antes de usar (evita 500/NPE).
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        if (ideiaIdParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // SRV-IDOR-06: so o LIDER edita o texto oficial (antes, so restrito na UI).
        Usuario usuarioLogado = new UsuarioDAO().buscar((Long) codigoUsuarioObj);
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(usuarioLogado, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        session.setAttribute("ideiaId", ideia.getCodigo());
        session.setAttribute("ideiaTitulo", ideia.getTitulo());

        LogColaboracao lc = new LogColaboracao();
        lc.setIdeia(ideia);

        // STM-01: buscarDescricaoFinal pode voltar null -- usa a descricao da ideia como fallback.
        LogColaboracao descFinal = new LogColaboracaoDAO().buscarDescricaoFinal(lc);
        String descricao;
        if (descFinal != null && descFinal.getDescricao() != null) {
            descricao = descFinal.getDescricao();
        } else if (ideia.getDescricao() != null) {
            descricao = ideia.getDescricao();
        } else {
            descricao = "";
        }

        session.setAttribute("ideiaDescricao", "<p>" + descricao + "</p>");

        response.sendRedirect(request.getContextPath() + File.separator + "editar-texto.jsp");
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
