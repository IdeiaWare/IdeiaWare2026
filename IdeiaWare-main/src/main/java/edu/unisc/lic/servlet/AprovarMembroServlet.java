package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AprovarMembroServlet", urlPatterns = {"/AprovarMembroServlet"})
public class AprovarMembroServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

        IdeiaUsuario vinculo = null;
        try {
            vinculo = ideiaUsuarioDAO.buscar(Long.parseLong(request.getParameter("vinculo")));
        } catch (NumberFormatException e) {
            vinculo = null;
        }
        if (vinculo == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
            return;
        }

        Ideia ideia = vinculo.getIdeia();

        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuario);
        List<IdeiaUsuario> souLider = ideiaUsuarioDAO
                .listarParametro(new IdeiaUsuario(sessionUser, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
            return;
        }

        if (StatusIdeia.VINCULO_PENDENTE.equals(vinculo.getFlStatusVinculo())) {
            vinculo.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
            vinculo.setMotivoRejeicaoMembro(null);
            ideiaUsuarioDAO.editar(vinculo);
        }

        response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "M.2: lider aprova a entrada de um membro no grupo (P -> A).";
    }
}
