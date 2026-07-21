package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EntrarIdeiaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // COLM-05: exige sessao valida e codigo numerico
        Object codigoUsuarioObj = request.getSession().getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        long codigoIdeia;
        try {
            codigoIdeia = Long.parseLong(request.getParameter("codigo"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar((Long) codigoUsuarioObj);

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(codigoIdeia);

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

        // COL-DUP: nao cria vinculo duplicado
        List<IdeiaUsuario> jaVinculado = ideiaUsuarioDAO
                .listarParametro(new IdeiaUsuario(usuario, ideia, null));
        if (jaVinculado != null && !jaVinculado.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        IdeiaUsuario ideiaUsuario = new IdeiaUsuario(usuario, ideia, "N");
        ideiaUsuario.setFlStatusVinculo(edu.unisc.lic.classes.StatusIdeia.VINCULO_PENDENTE);
        ideiaUsuario.setDtInscricao();
        try {
            ideiaUsuarioDAO.salvar(ideiaUsuario);
        } catch (org.hibernate.exception.ConstraintViolationException ex) {
            // K.8 #2: UNIQUE do banco barra o 2o insert
        }

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
