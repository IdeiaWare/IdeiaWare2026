package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CadastroIdeiaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // COL-15: validação server-side — não depende apenas do JavaScript do cliente
        String titulo    = request.getParameter("titulo");
        String descricao = request.getParameter("descricao");

        if (titulo == null || titulo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_obrigatorio");
            return;
        }
        if (titulo.trim().length() > 50) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_longo");
            return;
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=descricao_obrigatoria");
            return;
        }
        if (descricao.trim().length() > 200) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=descricao_longa");
            return;
        }

        HttpSession session = request.getSession(true);
        Usuario usuario = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));

        Ideia ideia = new Ideia();
        ideia.setTitulo(titulo.trim());
        ideia.setDescricao(descricao.trim());
        ideia.setDtCriacao();
        ideia.setUsuario(usuario);
        ideia.setStatus(StatusIdeia.PENDENTE);
        ideia.setStatusGrupo(StatusIdeia.GRUPO_ABERTO);

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        ideiaDAO.salvar(ideia);

        IdeiaUsuario ideiaUsuario = new IdeiaUsuario(usuario, ideia, "S");
        ideiaUsuario.setDtInscricao();
        new IdeiaUsuarioDAO().salvar(ideiaUsuario);

        response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
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
