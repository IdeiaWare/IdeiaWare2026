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
import javax.servlet.http.HttpSession;

public class EntrarDetalheServlet extends HttpServlet {

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

        // COLM-05: valida o código antes de converter.
        long codigo;
        try {
            codigo = Long.parseLong(request.getParameter("codigo"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar(codigo);
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-ideia.jsp");
            return;
        }

        // SRV-IDOR-03: "lider" calculado no servidor, nao mais confiado de parametro do form.
        Usuario usuarioLogado = new UsuarioDAO().buscar((Long) codigoUsuarioObj);
        List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(usuarioLogado, ideia, null));
        String lider = (vinculo != null && !vinculo.isEmpty()) ? vinculo.get(0).getFlLider() : "N";

        session.setAttribute("ideia", ideia);
        session.setAttribute("lider", lider);

        response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");

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
