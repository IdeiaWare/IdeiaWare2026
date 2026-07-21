package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ValidarIdeiaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // COL-07: validar ideia e acao de gestor
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario sessionUser = new UsuarioDAO().buscar((Long) session.getAttribute("codigoUsuario"));
        if (sessionUser == null || !"adm".equals(sessionUser.getPermissao())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        UsuarioDAO usuDAO = new UsuarioDAO();

        // BLINDA-03: parametros invalidos/nulos
        Ideia ideia;
        Usuario usu;
        try {
            ideia = ideiaDAO.buscar(Long.valueOf(request.getParameter("codigo")));
            usu = usuDAO.buscar(Long.valueOf(request.getParameter("codUsuario")));
        } catch (NumberFormatException | NullPointerException ex) {
            response.sendRedirect(request.getContextPath() + "/validar-ideia.jsp");
            return;
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + "/validar-ideia.jsp");
            return;
        }

        ideia.setGestor(usu);

        String acao = request.getParameter("validar");
        if ("validar".equals(acao) || "reabrir".equals(acao)) {
            ideia.setStatus(StatusIdeia.VALIDADA);
            ideia.setDtValidacao();
            ideia.setStatusGrupo(StatusIdeia.GRUPO_ABERTO);
            if ("reabrir".equals(acao)) {
                ideia.setMotivoRejeicao(null);
            }
        } else {
            ideia.setStatus(StatusIdeia.REJEITADA);
            ideia.setStatusGrupo(StatusIdeia.GRUPO_FECHADO);
            ideia.setDtRejeicao();
            ideia.setMotivoRejeicao(request.getParameter("motivo"));
        }

        ideiaDAO.editar(ideia);

        response.sendRedirect(request.getContextPath() + File.separator + "validar-ideia.jsp");
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
